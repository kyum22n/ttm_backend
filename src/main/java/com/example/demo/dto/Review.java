package com.example.demo.dto;

import lombok.Data;

@Data
public class Review {
	// FK
	private Integer requestOneId;	//	1:1 리뷰 건 수
	private Integer rWriterId;	// 리뷰 작성자
	private Integer rPostId;	// 산책 메이트 모집글
	private Integer targetId;	// 리뷰 당하는 사람
	// 
	// 산책 후기 식별 번호
	private Integer reviewId;
	// 산책 후기 태그
	private String reviewTagId;
}
