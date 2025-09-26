package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.PetDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dto.Pet;
import com.example.demo.dto.User;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	@Autowired
	private UserDao userDao;

	@Autowired
	private PetDao petDao;

	// 입력받은 회원 정보, 펫 정보 유효성 검증 후 각각의 테이블에 insert
	@Transactional
	public String join(User user, Pet pet) throws Exception {
		
		// Bcrypt 방식으로 암호화
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getUserPassword());

		// 사용자 비밀번호 값 세팅
		user.setUserPassword(encodedPassword);
		
		// 로그인 아이디와 이메일 중복 체크
		User dbUser = userDao.selectUserByLoginId(user.getUserLoginId());

		if (user.getUserLoginId().equals(dbUser.getUserLoginId())) {
			return "existID";	//이미 존재하는 아이디
			
		} else if(user.getUserEmail().equals(dbUser.getUserEmail())) {
			return "existEmail";	//이미 존재하는 이메일
			
		} else {
			// 회원 정보와 반려견 정보를 DB에 저장
			userDao.insert(user);
			petDao.insertPet(pet);
			if(pet.getPetAttachData() != null && pet.getPetAttachData().length > 0) {
				petDao.insertPetImage(pet);
			}

			return "success";
		}

		// !!! 회원가입 할때 지역, 생일은 어떤식으로 넣어야할까
		// 지역은 외부 주소 찾기 api를 사용해서 넣어야 함
		// 생일도 캘린더 띄워서 넣어야 함
	}

	public User info(Integer userId) {
		User user = userDao.selectByUserId(userId);
		return user;
	}

	public User update(User user) {
		User dbUser = userDao.selectByUserId(user.getUserId());

		if (dbUser == null) {
			return null;
		} else {
			if (StringUtils.isNotBlank(user.getUserName())) {
				dbUser.setUserName(user.getUserName());
			}
			if (user.getUserName() == null) {
				dbUser.setUserName(dbUser.getUserName());
			}
			if (StringUtils.isNotBlank(user.getUserPassword())) {
				dbUser.setUserPassword(user.getUserPassword());
			}
			if (StringUtils.isNotBlank(user.getUserAddress())) {
				dbUser.setUserAddress(user.getUserAddress());
			}
		}
		userDao.update(dbUser);
		return dbUser;
	}

	public enum RemoveResult {
		SUCCESS, FAIL
	}

	public RemoveResult remove(Integer userId) {
		int rows = userDao.delete(userId);
		if (rows == 0) {
			return RemoveResult.FAIL;
		} else {
			return RemoveResult.SUCCESS;
		}
	}

	public boolean isEnglishOnly(String input) {
		if (input == null)
			return false;
		return input.matches("^[a-zA-Z0-9]+$");
	}
}

// 반려동물 등록할때 uid 널허용
