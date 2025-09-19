package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatRoom {
	// 채팅방
	private Integer chatroomId;	// 채팅방 식별 번호
	private enum status{
		P,A,B,D
	};	// 채팅 신청 처리 상태(대기, 수락, 차단, 삭제)
	private	LocalDateTime updatedAt;	// 마지막 대화시간
	private LocalDateTime roomCreatedAt;	// 채팅방 생성일

}
