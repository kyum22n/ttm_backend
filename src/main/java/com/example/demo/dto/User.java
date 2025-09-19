package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
	// 사용자 식별 번호
	private Integer userId;
	// 사용자 로그인 아이디
	private String loginId;
	// 사용자 비밀번호
	private String password;
	// 사용자 이름
	private String uname;
	// 사용자 이메일
	private String email;
	// 사용자 지역
	private String address;
	// 사용자 생년월일
	private LocalDate birthDate;
	// 회원가입일
	private LocalDateTime createdAt;
	
}
