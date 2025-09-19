package com.example.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Chat {
	// 채팅방
	private Integer chatroomId;
	private Enum status;
	private	Date updatedAt;
	private Date roomCreatedAt;
	
	// 메시지
	private String messageContent;
	private boolean isRead;
	private Date readAt;
	private Date messageCreatedAt;
}
