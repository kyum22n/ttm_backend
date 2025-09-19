package com.example.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ChatRoom {
	// 채팅방
	private Integer chatroomId;
	private enum status{
		P,A,B,D
	};
	private	Date updatedAt;
	private Date roomCreatedAt;

}
