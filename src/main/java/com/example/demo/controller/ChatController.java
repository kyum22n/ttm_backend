package com.example.demo.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.ChatMessage;
import com.example.demo.dto.ChatRoom;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    // ------------------------------
    // WebSocket (STOMP) Endpoints
    // ------------------------------

    /**
     * 클라이언트 → 서버: /app/chat.sendMessage 로 전송
     * 서버 → 구독중인 클라이언트: /topic/chatroom.{chatroomId}
     */

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage chatMessage) {
        chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSend(
                "/topic/chatroom." + chatMessage.getChatroomId(),
                chatMessage);
    }

    // ------------------------------
    // REST API Endpoints
    // ------------------------------

    /**
     * 특정 채팅방의 메시지 기록 조회
     * GET /chat/{chatroomId}/messages
     */
    @GetMapping("/chat/{chatroomId}/messages")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable int chatroomId) {
        return chatMessageService.getMessages(chatroomId);
    }

    /**
     * 특정 유저가 속한 채팅방 전체 조회
     * GET /chat/rooms/{userId}
     */
    @GetMapping("/chat/rooms/{userId}")
    @ResponseBody
    public List<ChatRoom> getRoomsByUser(@PathVariable int userId) {
        return chatRoomService.getRoomsByUserId(userId);
    }

    /**
     * 채팅방 단건 조회
     * GET /chat/room/{chatroomId}
     */
    @GetMapping("/chat/room/{chatroomId}")
    @ResponseBody
    public ChatRoom getRoom(@PathVariable int chatroomId) {
        return chatRoomService.getRoomById(chatroomId);
    }

    /**
     * 채팅방 생성
     * POST /chat/room
     */
    @PostMapping("/chat/room")
    @ResponseBody
    public int createRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.createRoom(chatRoom);
    }

    /**
     * 채팅방 상태 변경 (예: 'A' 승인, 'B' 차단 등)
     * PUT /chat/room
     */
    @PutMapping("/chat/room")
    @ResponseBody
    public int updateRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.updateRoom(chatRoom);
    }

    /**
     * 채팅방 삭제
     * DELETE /chat/room/{chatroomId}
     */
    @DeleteMapping("/chat/room/{chatroomId}")
    @ResponseBody
    public int deleteRoom(@PathVariable int chatroomId) {
        return chatRoomService.deleteRoom(chatroomId);
    }
}