package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Pet;
import com.example.demo.dto.User;
import com.example.demo.service.UserService;
import com.example.demo.service.UserService.RemoveResult;

import lombok.extern.slf4j.Slf4j;

// /join /remove /info /update
// 회원가입 탈퇴 정보조회 정보수정
// 로그 확인 위해 어노테이션
@Slf4j
// REST 컨트롤러
@RestController
// /user가 기본 경로
@RequestMapping("/user")
public class UserController {
	// userService 주입
	@Autowired
	private UserService userService;

	// 회원가입
	// Post 요청
	// 데이터가 body에 담김
	@PostMapping("/join")
	// 폼 데이터와 멀티파트 파일을 자동으로 바인딩함
	// 멀티파트, 그리고 여러객체 떄문에 사용하게 됨
	// multipartfioele 처리 할때 예외 처리 될 수 있음
	public Map<String, Object> userJoin(@ModelAttribute User user, Pet pet) throws Exception {
		// 입력 값 확인
		log.info(user.toString());

		// 결과를 map으로 돌려줌
		Map<String, Object> map = new HashMap<>();

		String result = userService.join(user, pet);

		// 결과에 따라 메세지 출력
		if ("success".equals(result)) {
			map.put("result", "success");
		} else if ("existBoth".equals(result)) {
			map.put("result", "fail");
			map.put("message", "이미 존재하는 아이디와 이메일입니다.");
		} else if ("existID".equals(result)) {
			map.put("result", "fail");
			map.put("message", "이미 존재하는 아이디입니다.");
		} else if ("existEmail".equals(result)) {
			map.put("result", "fail");
			map.put("message", "이미 존재하는 이메일입니다.");
		} else {
			map.put("result", "fail");
			map.put("message", "회원가입 실패");
		}

		return map;
	}

	// Get 요청
	// url에 데이터 담김
	@GetMapping("/info")
	// url에 쿼리 파라미터 작성
	// 파라미터로 보냄
	public Map<String, Object> userInfo(@RequestParam("userId") Integer userId) {
		Map<String, Object> resultMap = new HashMap<>();
		User user = userService.info(userId);
		if (user == null) {
			resultMap.put("result", "fail");
			resultMap.put("message", "존재하지 않는 회원입니다.");
			return resultMap;
		} else {
			resultMap.put("result", "success");
			resultMap.put("data", user);
		}
		return resultMap;
	}

	@PutMapping("/update")
	// 이거 modify로 바꾸면 안될까요?
	// 요청 바디로 보냄
	public Map<String, Object> userUpdate(@RequestBody User user) {
		Map<String, Object> map = new HashMap<>();

		// userId로 회원 존재 여부 확인
		User dbUser1 = userService.info(user.getUserId());
		// 만약에 dbUser1가 null이면 존재하지 않는 회원
		if (dbUser1 == null) {
			map.put("result", "fail");
			map.put("message", "존재하지 않는 회원입니다.");
			return map;
		}

		User dbUser2 = userService.update(user);
		// update가 실패하면 null 반환
		if (dbUser2 == null) {
			map.put("result", "fail");
			map.put("message", "회원 정보 수정에 실패했습니다.");
			return map;
		}
		// 성공
		else {
			map.put("result", "success");
			map.put("data", dbUser2);
		}

		return map;
	}

	// userId로 회원 탈퇴
	// pathvariable로 보냄
	// url 경로의 값을 메서드 변수에 바인딩
	@DeleteMapping("/remove/{userId}")
	public String userRemove(@PathVariable("userId") Integer userId) {
		RemoveResult removeResult = userService.remove(userId);

		// 결과 값 json으로 반환
		JSONObject jsonObject = new JSONObject();

		// enum 값 비교
		if (removeResult == RemoveResult.SUCCESS) {
			jsonObject.put("result", "success");
		} else {
			jsonObject.put("result", "fail");
		}
		return jsonObject.toString();
	}

}
