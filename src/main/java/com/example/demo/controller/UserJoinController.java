package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.User;
import com.example.demo.service.UserJoinService;
import com.example.demo.service.UserJoinService.RemoveResult;

import lombok.extern.slf4j.Slf4j;

// /join /remove /info /update
// 회원가입 탈퇴 정보조회 정보수정
@Slf4j
@RestController
@RequestMapping("/user")
public class UserJoinController {

	@Autowired
	private UserJoinService userJoinService;

	@PostMapping("/join")
	public Map<String, Object> userJoin(@RequestBody User user) {
		// 입력 값 확인
		log.info(user.toString());

		Map<String, Object> map = new HashMap<>();

		// 암호화
		try {
			// Bcrypt 방식으로 암호화
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(user.getUserPassword());
			// user 객체의 필드 값 수정
			user.setUserPassword(encodedPassword);
			// userService를 통해 DB에 저장
			userJoinService.join(user);
			map.put("result", "success");
		} catch (Exception e) {
			map.put("result", "fail");
			map.put("message", e.getMessage());
		}
		return map;
	}

	@GetMapping("/info")
	public Map<String, Object> userInfo(@RequestParam("userId") Integer userId) {
		Map<String, Object> resultMap = new HashMap<>();
		User user = userJoinService.info(userId);
		resultMap.put("result", "success");
		// fail 처리도 해야되나요..?
		resultMap.put("data", user);
		return resultMap;
	}

	@PutMapping("/update")
	// 이거 modify로 바꾸면 안될까요?
	public Map<String, Object> userUpdate(@RequestBody User user) {
		User dbUser = userJoinService.update(user);

		Map<String, Object> map = new HashMap<>();

		// 만약에 dbUser가 null이면 존재하지 않는 회원
		if (dbUser == null) {
			map.put("result", "fail");
			// message도 넣어줄까요?
			map.put("message", "존재하지 않는 회원입니다.");
		} else {
			map.put("result", "success");
			map.put("data", dbUser);
		}
		return map;
	}

	@DeleteMapping("/remove/{userId}")
	public String userRemove(@PathVariable("userId") Integer userId) {
		RemoveResult removeResult = userJoinService.remove(userId);

		JSONObject jsonObject = new JSONObject();

		if (removeResult == RemoveResult.SUCCESS) {
			jsonObject.put("result", "success");
		} else {
			jsonObject.put("result", "fail");
		}
		return jsonObject.toString();
	}

}
