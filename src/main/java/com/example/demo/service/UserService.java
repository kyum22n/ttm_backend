package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.PetDao;
import com.example.demo.dao.PetImageDao;
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

	@Autowired
	private PetImageDao petImageDao;

	// 입력받은 회원 정보, 펫 정보 유효성 검증 후 각각의 테이블에 insert
	@Transactional
	public String join(User user, Pet pet) throws Exception {

		// Bcrypt 방식으로 암호화
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getUserPassword());

		// 사용자 비밀번호 값 세팅
		user.setUserPassword(encodedPassword);

		// 로그인 아이디와 이메일 중복 체크
		User dbUser = userDao.selectUserByLoginId(user.getUserLoginId()); // user.getUserLoginId()는 사용자가 입력한 아이디
																			// selectUserByLoginId는 DB값 있는지 검색 없으면 null 
		User dbUser2 = userDao.selectUserByEmail(user.getUserEmail());
		if (dbUser != null && dbUser2 != null) {
			return "existBoth"; // 아이디 + 이메일 둘 다 중복;
		} else if (dbUser != null) {
			return "existID"; // 이미 존재하는 아이디

		} else if (dbUser2 != null) {
			return "existEmail"; // 이미 존재하는 이메일

		} else {
			// 회원 정보와 반려견 정보를 DB에 저장
			userDao.insert(user);

			// Pet에 userId 세팅 후 insert
			pet.setPetUserId(user.getUserId());
			petDao.insertPet(pet);

			MultipartFile mf = pet.getPetAttach();
			if (mf != null && !mf.isEmpty()) {
				pet.setPetAttachOname(mf.getOriginalFilename());
				pet.setPetAttachType(mf.getContentType());
				pet.setPetAttachData(mf.getBytes());

				petImageDao.insertPetImage(pet);
			}

			Pet dbPet = petDao.selectByPetId(pet.getPetId());
			Pet image = petImageDao.selectPetImageByPetId(pet.getPetId());

			// image의 객체를 조회 하여 값이 있을 경우 받아온 정보를 dpPet에 추가함
			if (image != null) {
				dbPet.setPetImageId(image.getPetImageId());
				dbPet.setPetAttachOname(image.getPetAttachOname());
				dbPet.setPetAttachType(image.getPetAttachType());
				dbPet.setPetAttachData(image.getPetAttachData());
			}

			return "success";
		}

	}

	// !!! 회원가입 할때 지역, 생일은 어떤식으로 넣어야할까
	// 지역은 외부 주소 찾기 api를 사용해서 넣어야 함
	// 생일도 캘린더 띄워서 넣어야 함

	public User info(Integer userId) {
		User user = userDao.selectByUserId(userId);
		return user;
	}

	@Transactional
	public User update(User user) {
		User dbUser = userDao.selectByUserId(user.getUserId());

		if (dbUser == null) {
			return null;
		}
		if (StringUtils.isNotBlank(user.getUserName())) {
			dbUser.setUserName(user.getUserName());
		}
		if (user.getUserName() == null) {
			dbUser.setUserName(dbUser.getUserName());
		}
		if (StringUtils.isNotBlank(user.getUserPassword())) {
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(user.getUserPassword());
			dbUser.setUserPassword(encodedPassword);
		}
		if (StringUtils.isNotBlank(user.getUserAddress())) {
			dbUser.setUserAddress(user.getUserAddress());
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
