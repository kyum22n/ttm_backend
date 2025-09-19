package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Pet {
	// 반려견 식별 번호
	private Integer petId;
	// 반려견 몸무게
	private Integer weight;
	// 반려견 품종
	private String breed;
	// 반려견 생년월일
	private LocalDate petBirthDate;
	// 반려견 이름
	private String petName;
	// 반려견 소개말
	private String petDesc;
	// 반려견 좋아요 수
	private Integer petLikeCount;
	// 반려견 성별
	private enum petGender {
		M, F
	};
}
