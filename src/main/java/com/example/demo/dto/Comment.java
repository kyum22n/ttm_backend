package com.example.demo.dto;

import lombok.Data;

@Data
public class Comment {
	// 댓글
	private Integer commentId;	// 댓글 식별 번호
	private String commentContent;	// 댓글 내용
}
