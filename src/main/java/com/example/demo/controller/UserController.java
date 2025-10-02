package com.example.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	// HTTP 응답 상태 코드, 헤더, 바디를 직접 제어하기 위해
	// ResponseEntity
	public ResponseEntity<Object> userJoin(@ModelAttribute User user, Pet pet) throws IOException {
		// 입력 값 확인
		// log.info(user.toString());

		// 결과를 map으로 돌려줌
		Map<String, Object> map = new HashMap<>();

		userService.join(user, pet);
		map.put("result", "success");
		return ResponseEntity.ok(map);

	}

	@GetMapping("/check-duplicate")
	public ResponseEntity<Map<String, Object>> checkDuplicate(
			@RequestParam(name = "loginId", required = false) String loginId,
			@RequestParam(name = "email", required = false) String email) {
		Map<String, Object> map = new HashMap<>();
		userService.checkDuplicate(loginId, email);
		map.put("result", "success");
		return ResponseEntity.ok(map);
	}

	// Get 요청
	// url에 데이터 담김
	@GetMapping("/info")
	// url에 쿼리 파라미터 작성
	// 파라미터로 보냄
	public ResponseEntity<Object> userInfo(@RequestParam("userId") Integer userId) {
		Map<String, Object> map = new HashMap<>();
		User user = userService.info(userId);

		map.put("result", "success");
		map.put("data", user);

		return ResponseEntity.ok(map);
	}

	@PutMapping("/update")
	// 요청 바디로 보냄
	public ResponseEntity<Object> userUpdate(@RequestBody User user) {
		Map<String, Object> map = new HashMap<>();

		// userId로 회원 존재 여부 확인
		// 만약에 dbUser1가 null이면 존재하지 않는 회원
		User dbUser = userService.update(user);
		// update가 실패하면 null 반환
		// 성공
		map.put("result", "success");
		map.put("data", dbUser);
		return ResponseEntity.ok(map);
	}

	// userId로 회원 탈퇴
	// pathvariable로 보냄
	// url 경로의 값을 메서드 변수에 바인딩
	// 일단은 pet, post 정보 삭제하지 말고 user정보만 삭제하기
	@DeleteMapping("/remove/{userId}")
	public ResponseEntity<Object> userRemove(@PathVariable("userId") Integer userId) {
		Map<String, Object> map = new HashMap<>();

		// userId로 회원 존재 여부 확인
		User dbUser = userService.remove(userId);
		// 성공
		map.put("result", "success");
		map.put("data", dbUser);

		return ResponseEntity.ok(map);
	}

}
