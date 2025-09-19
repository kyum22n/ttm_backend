package com.example.demo.dto;

import lombok.Data;

@Data
public class Comment {
	// 댓글
	// FK
	private Integer cpostId;	// 댓글이 달리는 게시물
	private Integer cwriter;	// 댓글 작성자
	// 
	private Integer commentId;	// 댓글 식별 번호
	private String commentContent;	// 댓글 내용
}
