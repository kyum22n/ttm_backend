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
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/user-login")
public class UserLoginController {
  @Autowired
  private UserService userService;

  @PostMapping("/login")
  public User login(@RequestBody Loginform loginForm) {

    // 1. 아이디로 사용자 조회
    User user = userService.getUserByLoginId(loginForm.getLoginId());
    log.info("user: {}", user);

    // 이렇게 사용하면 User의 객체를 받아왔는데 또 다시 userId를 통해 객체를 찾게 되므로 DB에서 2번 조회하는 꼴이므로 사용x
    // User user = userService.getUserById(user.getUserId());

    // 2. 아이디 존재 여부 확인
    if(user == null){
      throw new RuntimeException("존재하지 않는 아이디입니다.");
    }
    // 3. 비밀번호 검증
    if(!loginForm.getPassword().equals(user.getUserPassword())){
      throw new RuntimeException("비밀번호가 일치하지 않습니다.");
    }
    return user;
  }
  
  @PostMapping("/find-id")
  public Map<String, Object> findId(@Valid @RequestBody FindIdForm findIdForm) {
    // @Valid 사용: 공백/잘못된 이메일 형식 자동 검증
    Map<String, Object> map = new HashMap<>();
    
    // 1. 이메일로 사용자 조회
    User user = userService.getUserByEmail(findIdForm.getEmail());

    if(user == null){ // 이메일로 가입된 유저가 없음
      map.put("message", "해당 이메일로 가입된 계정이 없습니다.");
    } else{ // 이메일 인증 성공시 id제공
      map.put("loginId", user.getUserLoginId());
      map.put("message", "회원님의 아이디는 " + user.getUserLoginId() +" 입니다.");
    }
      
    return map;
  }
  
  @PostMapping("/find-password")
  public Map<String, Object> findPassword(@RequestBody FindPasswordForm findPasswordForm) {
    Map<String, Object> map = new HashMap<>();

    User user= userService.getUserByLoginId(findPasswordForm.getLoginId());
    // 1. 아이디로 사용자 조회
    if(user == null){
      map.put("message", "존재하지 않는 아이디입니다.");
      return map;
    }
    // 2. 임시 비밀번호 발급 & 메일 전송
    userService.sendTempPassword(user);

    // 3. 사용자 응답 메시지
    map.put("message", "임시 비밀번호가 이메일로 전송되었습니다.");
    return map;
  }
  

}
