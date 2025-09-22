package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
	// 사용자 식별 번호
	private Integer userId;
	// 사용자 로그인 아이디
	private String userLoginId;
	// 사용자 비밀번호
	private String userPassword;
	// 사용자 이름
	private String userName;
	// 사용자 이메일
	private String userEmail;
	// 사용자 지역
	private String userAddress;
	// 사용자 생년월일
	private LocalDate userBirthDate;
	// 회원가입일
	private LocalDateTime createdAt;
	
}
