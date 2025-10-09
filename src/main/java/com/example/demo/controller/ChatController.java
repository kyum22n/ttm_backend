package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ChatMessage;
import com.example.demo.dto.ChatReadReq;
import com.example.demo.dto.ChatRoom;
import com.example.demo.dto.ChatSendReq;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    /** 방 보장: 기존(P/A)이 있으면 그 방, 없으면 P로 생성 */
    @PostMapping("/rooms/ensure")
    public ResponseEntity<?> ensureRoom(
            @RequestParam("userA") int userA,
            @RequestParam("userB") int userB,
            @RequestParam("requestedBy") int requestedBy) {

        ChatRoom room = chatRoomService.ensurePairRoom(userA, userB, requestedBy);
        return ResponseEntity.ok(Map.of("result", "success", "room", room));
    }

    /** 방 정보: 멤버면 항상 200 + room 반환 (canChat = A 여부) */
    @GetMapping("/rooms/{roomId}/info")
    public ResponseEntity<?> roomInfo(
            @PathVariable int roomId,
            @RequestParam("userId") int userId) {
                System.out.println(">>> [roomInfo] HIT roomId=" + roomId + ", userId=" + userId);
        ChatRoom r = chatRoomService.getById(roomId);
        if (r == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("result", "fail", "code", "NOT_FOUND", "message", "room not found"));
        }
        if (!chatRoomService.isMember(roomId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("result", "fail", "code", "NOT_MEMBER", "message", "not a member"));
        }
        boolean canChat = "A".equals(r.getChatroomStatus());
        return ResponseEntity.ok(Map.of("result", "success", "room", r, "canChat", canChat));
    }

    /** 승인 */
    @PutMapping("/rooms/{roomId}/approve")
    public ResponseEntity<?> approve(@PathVariable int roomId, @RequestParam("by") int by) {
        try {
            chatRoomService.approve(roomId, by);
            return ResponseEntity.ok(Map.of("result", "success", "status", "A"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("result", "fail", "code", "NOT_MEMBER"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("result", "fail", "code", "NOT_FOUND"));
        }
    }

    /** 거절(닫기) */
    @PutMapping("/rooms/{roomId}/reject")
    public ResponseEntity<?> reject(@PathVariable int roomId, @RequestParam("by") int by) {
        try {
            chatRoomService.reject(roomId, by);
            return ResponseEntity.ok(Map.of("result", "success", "status", "D"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("result", "fail", "code", "NOT_MEMBER"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("result", "fail", "code", "NOT_FOUND"));
        }
    }

    /** 히스토리 (A 상태만) */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Map<String, Object>> history(
            @PathVariable int roomId,
            @RequestParam("userId") int userId,
            @RequestParam(value = "beforeMessageId", required = false) Integer beforeMessageId,
            @RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit) {
                System.out.println(">>> [roomInfo] HIT roomId=" + roomId + ", userId=" + userId);

        if (!chatRoomService.isMember(roomId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("result", "fail", "code", "NOT_MEMBER", "message", "not a member"));
        }
        if (!chatRoomService.isApproved(roomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("result", "fail", "code", "NOT_APPROVED", "message", "not approved yet"));
        }

        List<ChatMessage> list = chatMessageService.history(roomId, beforeMessageId, limit);
        Map<String, Object> body = new HashMap<>();
        body.put("result", "success");
        body.put("messages", list);
        return ResponseEntity.ok(body);
    }

    /** 읽음 처리 (A 상태만) */
    @PutMapping("/rooms/{roomId}/read")
    public ResponseEntity<Map<String, Object>> markRead(
            @PathVariable int roomId,
            @RequestBody ChatReadReq req) {

        if (!chatRoomService.isMember(roomId, req.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("result", "fail", "code", "NOT_MEMBER", "message", "not a member"));
        }
        if (!chatRoomService.isApproved(roomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("result", "fail", "code", "NOT_APPROVED", "message", "not approved yet"));
        }
        int updated = chatMessageService.markRead(roomId, req.getUserId(), req.getUpToMessageId());
        int unread = chatMessageService.unreadCount(roomId, req.getUserId());
        return ResponseEntity.ok(Map.of("result", "success", "updated", updated, "unread", unread));
    }

    /** STOMP 전송 (A 상태만) */
    @MessageMapping("/chat.send")
    public void onSend(ChatSendReq req) {
        Integer roomId = req.getRoomId();
        if (roomId == null && req.getOtherUserId() != null) {
            ChatRoom room = chatRoomService.ensurePairRoom(req.getSenderId(), req.getOtherUserId(), req.getSenderId());
            roomId = room.getChatroomId();
        }
        if (roomId == null)
            return;
        if (!chatRoomService.isMember(roomId, req.getSenderId()))
            return;
        if (!chatRoomService.isApproved(roomId))
            return;

        ChatMessage saved = chatMessageService.send(roomId, req.getSenderId(), req.getMessage());
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "NEW_MESSAGE");
        payload.put("roomId", roomId);
        payload.put("message", saved);
        messagingTemplate.convertAndSend("/topic/chat." + roomId, payload);
    }

    /** STOMP 읽음 (A 상태만) */
    @MessageMapping("/chat.read")
    public void onRead(ChatReadReq req) {
        if (!chatRoomService.isMember(req.getRoomId(), req.getUserId()))
            return;
        if (!chatRoomService.isApproved(req.getRoomId()))
            return;

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

    // 내가 참여하고 있는 채팅방 목록
    @GetMapping("/rooms/my")
    public ResponseEntity<?> myRooms(@RequestParam("userId") int userId) {
        // 서비스에서 목록 + 부가정보(미읽음/마지막 메시지 시간 등)까지 묶어 내려줄 수도 있고,
        // 일단은 방 리스트만 내려도 프론트에서 partnerId 계산해서 씀.
        var list = chatRoomService.findRoomsByUser(userId);
        return ResponseEntity.ok(Map.of("result", "success", "rooms", list));
    }

}
