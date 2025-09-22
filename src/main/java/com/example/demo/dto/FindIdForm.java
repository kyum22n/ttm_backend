package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindIdForm {
  @NotBlank(message = "이메일은 필수 입력 사항입니다.")
  @Email
  // 사용자 이메일
	private String email;
}
