package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    // 1) 1:1 방 보장(있으면 반환, 없으면 생성 후 A로)
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

    // 2) REST: 히스토리 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Map<String, Object>> history(
            @PathVariable("roomId") int roomId,
            @RequestParam("userId") int userId,
            @RequestParam(value = "beforeMessageId", required = false) Integer beforeMessageId,
            @RequestParam(value = "limit", required = false, defaultValue = "50") int limit) { // ← int로
        if (!chatRoomService.isMember(roomId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("result", "fail", "message", "not a member"));
        }
        List<ChatMessage> list = chatMessageService.recent(roomId, beforeMessageId, limit);
        return ResponseEntity.ok(Map.of("result", "success", "messages", list));
    }

    // 3) REST: 읽음 처리 + 안읽음 수
    @PutMapping("/rooms/{roomId}/read")
    public ResponseEntity<Map<String, Object>> markRead(
            @PathVariable("roomId") int roomId,
            @RequestBody ChatReadReq req) {

        if (!chatRoomService.isMember(roomId, req.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("result", "fail", "message", "not a member"));
        }
        int updated = chatMessageService.markRead(roomId, req.getUserId(), req.getUpToMessageId());
        int unread = chatMessageService.unreadCount(roomId, req.getUserId());

        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        map.put("updated", updated);
        map.put("unread", unread);
        return ResponseEntity.ok(map);
    }

    // 4) STOMP: 메시지 전송 (클라이언트 → /app/chat.send)
    @MessageMapping("/chat.send")
    public void onSend(ChatSendReq req) {
        // NullPointException을 방지 하기 위해
        Integer roomId = req.getRoomId();
        Integer senderId = req.getSenderId(); // ← 따로 꺼내서 null 체크
        Integer otherUserId = req.getOtherUserId();

        // roomId가 없고 otherUserId가 있으면 1:1방 보장 후 전송
        if (senderId == null || (roomId == null && otherUserId == null)) {
            log.warn("chat.send rejected: missing senderId or both roomId/otherUserId are null");
            return;
        }

        if (roomId == null) {
            ChatRoom room = chatRoomService.ensurePairRoom(senderId, otherUserId, senderId);
            roomId = room.getChatroomId();
        }

        if (!chatRoomService.isMember(roomId, senderId)) {
            log.warn("chat.send rejected: not a member (roomId={}, senderId={})", roomId, senderId);
            return;
        }

        // 저장
        ChatMessage saved = chatMessageService.send(roomId, req.getSenderId(), req.getMessage());

        // 브로드캐스트: /topic/chat.{roomId}
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "NEW_MESSAGE");
        payload.put("roomId", roomId);
        payload.put("message", saved);
        messagingTemplate.convertAndSend("/topic/chat." + roomId, payload);
    }

    // 5) STOMP: 읽음 처리 (클라 → /app/chat.read)
    @MessageMapping("/chat.read")
    public void onRead(ChatReadReq req) {
        if (req.getRoomId() == null || req.getUserId() == null || req.getUpToMessageId() == null) {
            return;
        }
        if (!chatRoomService.isMember(req.getRoomId(), req.getUserId())) {
            return;
        }
        int updated = chatMessageService.markRead(req.getRoomId(), req.getUserId(), req.getUpToMessageId());
        int unread = chatMessageService.unreadCount(req.getRoomId(), req.getUserId());

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "READ_UPDATE");
        payload.put("roomId", req.getRoomId());
        payload.put("updated", updated);
        payload.put("unread", unread);
        payload.put("readerId", req.getUserId());
        messagingTemplate.convertAndSend("/topic/chat." + req.getRoomId(), payload);
    }
}
