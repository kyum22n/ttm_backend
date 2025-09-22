package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindPasswordForm {
  @NotBlank(message = "아이디는 필수 입력 사항입니다.")
  // 사용자 로그인 아이디
	private String loginId;
}
