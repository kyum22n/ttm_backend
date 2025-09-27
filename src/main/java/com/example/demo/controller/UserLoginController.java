package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.FindIdForm;
import com.example.demo.dto.FindPasswordForm;
import com.example.demo.dto.Loginform;
import com.example.demo.dto.User;
import com.example.demo.service.UserLoginService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user-login")
public class UserLoginController {
  @Autowired
  private UserLoginService userLoginService;

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody Loginform loginForm) {
    Map<String, Object> map = new HashMap<>();
    try {
      // 1.로그인 성공 시 jwt 토큰 발급
      String jwt = userLoginService.userLogin(loginForm.getLoginId(), loginForm.getPassword());
      // 2. 로그인 성공
      map.put("result", "success");
      map.put("loginId", loginForm.getLoginId());
      map.put("jwt", jwt);
    } catch (IllegalArgumentException e) { // 3. 로그인 실패
      map.put("result", "fail");
      map.put("message", e.getMessage());
    }
    return map;
  }

  @PostMapping("/find-id")
  public Map<String, Object> findId(@Valid @RequestBody FindIdForm findIdForm) {
    // @Valid 사용: 공백/잘못된 이메일 형식 자동 검증
    Map<String, Object> map = new HashMap<>();

    // 1. 이메일로 사용자 조회
    User user = userLoginService.getUserByEmail(findIdForm.getEmail());

    if (user == null) { // 이메일로 가입된 유저가 없음
      map.put("message", "해당 이메일로 가입된 계정이 없습니다.");
    } else { // 이메일 인증 성공시 id제공
      map.put("loginId", user.getUserLoginId());
      map.put("message", "회원님의 아이디는 " + user.getUserLoginId() + " 입니다.");
    }

    return map;
  }

  @PostMapping("/find-password")
  public Map<String, Object> findPassword(@RequestBody FindPasswordForm findPasswordForm) {
    Map<String, Object> map = new HashMap<>();

    User user = userLoginService.getUserByLoginId(findPasswordForm.getLoginId());
    // 1. 아이디로 사용자 조회
    if (user == null) {
      map.put("message", "존재하지 않는 아이디입니다.");
      return map;
    } else {
      // 2. 임시 비밀번호 발급 & 메일 전송
      userLoginService.sendTempPassword(user);

      // 3. 사용자 응답 메시지
      map.put("message", "임시 비밀번호가 이메일로 전송되었습니다.");
      return map;
    }
  }

}
