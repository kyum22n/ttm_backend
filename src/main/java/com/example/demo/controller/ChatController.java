package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.*;
import com.example.demo.service.ChatRoomService;
import com.example.demo.service.ChatMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 1) 1:1 채팅방 보장 API
     * - 동일한 두 유저 조합의 활성/대기(P/A) 방이 있으면 그걸 반환
     * - 없으면 생성한 뒤 활성화하고 반환
     * - 테스트: POST /chat/rooms/ensure?userA=2&userB=3&requestedBy=2
     */
    @PostMapping("/rooms/ensure")
    public ResponseEntity<Map<String, Object>> ensureRoom(
            @RequestParam("userA") int userA,
            @RequestParam("userB") int userB,
            @RequestParam("requestedBy") int requestedBy) {

        ChatRoom room = chatRoomService.ensurePairRoom(userA, userB, requestedBy);

        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        map.put("room", room);
        return ResponseEntity.ok(map);
    }

    /**
     * 2) 대화 히스토리 조회 (키셋 페이징)
     * - 방 멤버만 조회 가능(멤버 검증)
     * - beforeMessageId(옵션): 해당 ID 미만의 과거 메시지 N개
     * - limit 기본 50, 서버 상한 100
     * - 응답은 최신 ↓ 순서로 반환(SELECT에서 DESC 후 ROWNUM 사용)
     *
     * 예: GET /chat/rooms/5/messages?userId=2&limit=30
     * GET /chat/rooms/5/messages?userId=2&beforeMessageId=200&limit=30
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Map<String, Object>> history(
            @PathVariable("roomId") int roomId,
            @RequestParam("userId") int userId,
            @RequestParam(value = "beforeMessageId", required = false) Integer beforeMessageId,
            @RequestParam(value = "limit", required = false, defaultValue = "50") int limit) {

        // 방 접근 권한(멤버 여부) 체크
        if (!chatRoomService.isMember(roomId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("result", "fail", "message", "not a member"));
        }

        // 과도한 요청 방지(간단 서버 가드)
        if (limit > 100)
            limit = 100;
        if (limit <= 0)
            limit = 50;

        List<ChatMessage> list = chatMessageService.recent(roomId, beforeMessageId, limit);
        return ResponseEntity.ok(Map.of("result", "success", "messages", list));
    }

    /**
     * 3) 읽음 처리 + 현재 미읽음 수 반환
     * - 본인이 보낸 메시지는 제외하고, upToMessageId 이하를 일괄 읽음 처리
     * - 멤버 검증 후 실행
     *
     * 예: PUT /chat/rooms/5/read
     * Body: { "userId": 2, "upToMessageId": 345 }
     */
    @PutMapping("/rooms/{roomId}/read")
    public ResponseEntity<Map<String, Object>> markRead(
            @PathVariable("roomId") int roomId,
            @RequestBody ChatReadReq req) {

        // 멤버 검증
        if (!chatRoomService.isMember(roomId, req.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("result", "fail", "message", "not a member"));
        }

        // 읽음 처리: 상대가 보낸, upTo 이하, 아직 미읽음인 행만 'Y'로
        int updated = chatMessageService.markRead(roomId, req.getUserId(), req.getUpToMessageId());
        // 현재 미읽음 카운트 반환(배지 업데이트 용)
        int unread = chatMessageService.unreadCount(roomId, req.getUserId());

        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        map.put("updated", updated);
        map.put("unread", unread);
        return ResponseEntity.ok(map);
    }

    /**
     * 4) STOMP: 메시지 전송 (클라 → /app/chat.send)
     * - roomId가 없고 otherUserId만 있으면 ensure 후 전송
     * - 멤버 검증 통과 시 DB 저장 → /topic/chat.{roomId} 로 브로드캐스트
     *
     * 클라 전송 페이로드 예:
     * { "roomId": 5, "senderId": 2, "message": "hi" }
     * 또는
     * { "otherUserId": 3, "senderId": 2, "message": "hi" }
     */
    @MessageMapping("/chat.send")
    public void onSend(ChatSendReq req) {
        Integer roomId = req.getRoomId();
        Integer senderId = req.getSenderId();
        Integer otherUserId = req.getOtherUserId();

        // 필수값 가드
        if (senderId == null || (roomId == null && otherUserId == null)) {
            log.warn("chat.send rejected: missing senderId or both roomId/otherUserId are null");
            return;
        }
        // 빈 메시지 거부
        if (req.getMessage() == null || req.getMessage().trim().isEmpty()) {
            log.warn("chat.send rejected: empty message");
            return;
        }

        // roomId가 없으면 1:1 방 보장 후 얻는다
        if (roomId == null) {
            ChatRoom room = chatRoomService.ensurePairRoom(senderId, otherUserId, senderId);
            roomId = room.getChatroomId();
        }

        // 멤버 검증
        if (!chatRoomService.isMember(roomId, senderId)) {
            log.warn("chat.send rejected: not a member (roomId={}, senderId={})", roomId, senderId);
            return;
        }

        // 메시지 저장 (MyBatis selectKey로 messageId 세팅됨)
        ChatMessage saved = chatMessageService.send(roomId, senderId, req.getMessage());

        // 브로드캐스트: 해당 방 구독자 모두 수신
        Map<String, Object> msgpayload = new HashMap<>();
        msgpayload.put("type", "NEW_MESSAGE");
        msgpayload.put("roomId", roomId);
        msgpayload.put("message", saved);
        messagingTemplate.convertAndSend("/topic/chat." + roomId, msgpayload);

        // 3) 보낸 시점까지 상대가 보낸 내 미읽음을 읽음 처리 + 배지 계산
        int updated = chatMessageService.markRead(roomId, senderId, saved.getMessageId());
        int unread = chatMessageService.unreadCount(roomId, senderId);

        // 4) READ_UPDATE 브로드캐스트 (배지/읽음 동기화)
        Map<String, Object> readPayload = new HashMap<>();
        readPayload.put("type", "READ_UPDATE");
        readPayload.put("roomId", roomId);
        readPayload.put("updated", updated);
        readPayload.put("unread", unread);
        readPayload.put("readerId", senderId);
        messagingTemplate.convertAndSend("/topic/chat." + roomId, readPayload);

        log.info("chat.send ok roomId={}, senderId={}, msgId={}, readUpdated={}, myUnread={}",
            roomId, senderId, saved.getMessageId(), updated, unread);
    }

    /**
     * 5) STOMP: 읽음 처리 (클라 → /app/chat.read)
     * - 클라이언트가 읽음 위치를 알려주면 DB 업데이트 후
     * /topic/chat.{roomId} 로 READ_UPDATE 브로드캐스트
     *
     * 클라 전송 페이로드 예:
     * { "roomId": 5, "userId": 2, "upToMessageId": 345 }
     */
    @MessageMapping("/chat.read")
    public void onRead(ChatReadReq req) {
        // 바디 필수값 가드(널 방지)
        if (req.getRoomId() == null || req.getUserId() == null || req.getUpToMessageId() == null) {
            return;
        }
        // 멤버 검증
        if (!chatRoomService.isMember(req.getRoomId(), req.getUserId())) {
            return;
        }

        // 읽음 처리 + 미읽음 카운트 갱신
        int updated = chatMessageService.markRead(req.getRoomId(), req.getUserId(), req.getUpToMessageId());
        int unread = chatMessageService.unreadCount(req.getRoomId(), req.getUserId());

        // 방 토픽으로 읽음 상태 변경 브로드캐스트
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "READ_UPDATE");
        payload.put("roomId", req.getRoomId());
        payload.put("updated", updated);
        payload.put("unread", unread);
        payload.put("readerId", req.getUserId());
        payload.put("upToMessageId", req.getUpToMessageId());
        messagingTemplate.convertAndSend("/topic/chat." + req.getRoomId(), payload);
    }
}
