package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatMessage {
	// 채팅 메시지
	// FK
	private Integer chatroomId; // 채팅방 식별 번호
	private Integer senderId; // 보낸 이
	// 
	private Integer messageId;	// 채팅 메시지 식별 번호
	private String messageContent;	// 채팅 메시지 내용
	private boolean isRead;	// 채팅 메시지 읽음 상태
	private LocalDateTime readAt;	// 메시지 읽은 시간
	private LocalDateTime messageCreatedAt;	// 메시지 보낸 시간
}
