package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class User {
	// 사용자 식별 번호
	private Integer userId;

	// 사용자 로그인 아이디 
	// @NotBlank(message = "아이디는 필수 사항입니다.")
	// @Size(min = 5, max = 10, message = "아이디는 5자 이상 10자 이하이어야 합니다.")
	private String userLoginId;

	// 사용자 비밀번호
	// @NotBlank(message = "mpassword는 필수 사항입니다.")
	// @Size(min = 5, max = 10, message = "mid는 5자 이상 10자 이하이어야 합니다.")
	// @Pattern(
	// 	regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).{5,10}$",
	// 	message = "비밀번호는 영어로 대소문자를 포함하고, 특수문자를 최소 1개 포함해야 합니다."
	// )
	private String userPassword;

	// 사용자 이름
	private String userName;

	// 사용자 이메일
	// @NotBlank(message = "memail는 필수 사항입니다.")
	// @Email
	private String userEmail;

	// 사용자 지역
	private String userAddress;
	// 사용자 생년월일
	private LocalDate userBirthDate;
	// 회원가입일
	private LocalDateTime createdAt;
	
}
