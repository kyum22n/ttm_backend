package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Pet {
	// 반려인 식별 번호(FK)
	private Integer puserId;
	// 반려견 식별 번호
	private Integer petId;
	// 반려견 몸무게
	private Integer petWeight;
	// 반려견 품종
	private String petBreed;
	// 반려견 생년월일
	private LocalDate petBirthDate;
	// 반려견 이름
	private String petName;
	// 반려견 소개말
	private String petDesc;
	// 반려견 좋아요 수
	private Integer petLikeCount;
	// 반려견 성별
	private String petGender;
	private LocalDateTime createdAt; // 반려견 정보 등록일

	// 반려견 이미지
	private Integer petImageId; // 반려견 이미지 식별 번호
	private MultipartFile petAttach; // 반려견 이미지
	private String petAttachOname; // 반려견 이미지 파일명
	private String petAttachType; // 반려견 이미지 파일 타입
	private byte[] petAttachData; // 반려견 이미지 데이터
}
