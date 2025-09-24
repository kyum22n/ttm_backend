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
import com.example.demo.service.UserLoginService;
import com.example.demo.service.UserService;
import com.example.demo.service.UserService.RemoveResult;

import lombok.extern.slf4j.Slf4j;

// /join /remove /info /update
// 회원가입 탈퇴 정보조회 정보수정
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserLoginService userLoginService;

	@PostMapping("/join")
	public Map<String, Object> userJoin(@RequestBody User user) {
		// 입력 값 확인
		log.info(user.toString());

		Map<String, Object> map = new HashMap<>();

		if (!userService.isEnglishOnly(user.getUserLoginId())) {
        map.put("result", "fail");
        map.put("message", "아이디는 영어로만 입력해야 합니다.");
        return map;
    }

    if (!userService.isEnglishOnly(user.getUserPassword())) {
        map.put("result", "fail");
        map.put("message", "비밀번호는 영어로만 입력해야 합니다.");
        return map;
    }

		// 암호화
		try {
			// Bcrypt 방식으로 암호화
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(user.getUserPassword());
			// user 객체의 필드 값 수정
			user.setUserPassword(encodedPassword);
			User user1 = userLoginService.getUserByLoginId(user.getUserLoginId());
			User user2 = userLoginService.getUserByEmail(user.getUserEmail());
			if (user1 != null) {
				map.put("result", "fail");
				map.put("message", "이미 사용중인 아이디입니다.");
				return map;
			}
			if (user2 != null) {
				map.put("result", "fail");
				map.put("message", "이미 등록된 이메일입니다.");
				return map;
			}
			// userService를 통해 DB에 저장
			userService.join(user);
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
		User user = userService.info(userId);
		if (user == null) {
			resultMap.put("result", "fail");
			resultMap.put("message", "존재하지 않는 회원입니다.");
			return resultMap;
			// 테스트용 강제 500 에러
			// throw new RuntimeErrorException(null, "forced500 for test");
		} else {
			resultMap.put("result", "success");
			resultMap.put("data", user);
		}
		return resultMap;
	}

	@PutMapping("/update")
	// 이거 modify로 바꾸면 안될까요?
	public Map<String, Object> userUpdate(@RequestBody User user) {
		Map<String, Object> map = new HashMap<>();

		// userId로 회원 존재 여부 확인
		User dbUser = userService.info(user.getUserId());
		// 만약에 dbUser가 null이면 존재하지 않는 회원
		if (dbUser == null) {
			map.put("result", "fail");
			map.put("message", "존재하지 않는 회원입니다.");
			return map;
		}

		if (!userService.isEnglishOnly(user.getUserPassword())) {
        map.put("result", "fail");
        map.put("message", "비밀번호는 영어로만 입력해야 합니다.");
        return map;
    }

		// 암호화
		if (user.getUserPassword() == null)
			user.setUserPassword(dbUser.getUserPassword());
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getUserPassword());
		// user 객체의 필드 값 수정
		user.setUserPassword(encodedPassword);

		dbUser = userService.update(user);
		// update가 실패하면 null 반환
		if (dbUser == null) {
			map.put("result", "fail");
			map.put("message", "회원 정보 수정에 실패했습니다.");
			return map;
		}
		// 성공
		else {
			map.put("result", "success");
			map.put("data", dbUser);
		}

		return map;
	}

	@DeleteMapping("/remove/{userId}")
	public String userRemove(@PathVariable("userId") Integer userId) {
		RemoveResult removeResult = userService.remove(userId);

		JSONObject jsonObject = new JSONObject();

		if (removeResult == RemoveResult.SUCCESS) {
			jsonObject.put("result", "success");
		} else {
			jsonObject.put("result", "fail");
		}
		return jsonObject.toString();
	}

}
