package com.example.demo.dto;

import lombok.Data;

@Data
public class Loginform {
  // 사용자 로그인 아이디
	private String loginId;
	// 사용자 비밀번호
	private String password;
}
