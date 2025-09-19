package com.example.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ChatMessage {

	// 메시지
	private String messageContent;
	private boolean isRead;
	private Date readAt;
	private Date messageCreatedAt;
}
