package com.example.demo.dto;

import lombok.Data;

@Data
public class Review {
	// 산책 후기 식별 번호
	private Integer reviewId;
	// 산책 후기 태그
	private String reviewTag;
}
