package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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

// 로깅 사용위한 어노테이션
@Slf4j
// 비즈니스 로직을 담당하는 서비스 계층임을 알림
@Service
public class UserService {
	// 의존성 주입 위해 사용
	@Autowired
	// DAO 객체 주입
	private UserDao userDao;

	@Autowired
	// 회원가입을 할때에 반려동물을 먼저 등록하기로 하였으나
	// 반려동물 테이블의 외래키로 userId 존재하여서
	// 결국 pet 테이블 insert 할때 userId를 세팅해줘야함
	// 따라서 petDao도 주입
	private PetDao petDao;

	@Autowired
	// 회원 가입을 할때에 펫 이미지가 프로필 사진으로 등록됨
	// 따라서 petImageDao도 주입
	private PetImageDao petImageDao;

	// 입력받은 회원 정보, 펫 정보 유효성 검증 후 각각의 테이블에 insert
	// 여러 쿼리가 들어가기 때문에 하나라도 실패하게 되면 돌아오게 하기 위해
	// 모든 쿼리가 정상 실행되면 그제서야 적용
	@Transactional
	// 위와 같은 이유로 pet이 userId를 외래키로 가지고 있기 때문에
	// user와 pet을 같이 받아서 처리
	public String join(User user, Pet pet) throws IOException {

		// Bcrypt 방식으로 암호화
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getUserPassword());

		// 사용자 비밀번호 값 세팅
		user.setUserPassword(encodedPassword);

		// 로그인 아이디와 이메일 중복 체크
		// 유저가 입력한 아이디가 DB에 존재하는지 확인하기 위해 select
		// selectUserByLoginId는 DB값 있는지 검색 없으면 null
		User dbUser = userDao.selectUserByLoginId(user.getUserLoginId()); // user.getUserLoginId()는 사용자가 입력한 아이디
		// 이메일도 동일하게 체크
		User dbUser2 = userDao.selectUserByEmail(user.getUserEmail());
		// 경우에 따라서 null이라면 중복이기에 함수 종료
		if (dbUser != null && dbUser2 != null) { // 아이디 + 이메일 둘 다 중복;
			throw new DuplicateKeyException("이미 존재하는 아이디와 이메일입니다.");
		} else if (dbUser != null) {// 이미 존재하는 아이디
			throw new DuplicateKeyException("이미 존재하는 아이디입니다.");

		} else if (dbUser2 != null) { // 이미 존재하는 이메일
			throw new DuplicateKeyException("이미 존재하는 이메일입니다.");
		} else {
			// 회원 정보와 반려견 정보를 DB에 저장
			userDao.insert(user);
			// 실패하면 spring + myBatis가 내부에서 SQL Exception 을 잡아서
			// 적절한 런타임 에러 (ex)DataIntegrityViolationException) 을 넣어줌
			// 그리고 GlobalExceptionHandler에서 처리함
			// 따라서 따로 throw 안해도 됨

			// Pet에 userId 세팅 후 insert
			// Pet에 userId는 외래키이기 때문
			pet.setPetUserId(user.getUserId());
			petDao.insertPet(pet);

			// pet 이미지 파일을 첨부 받기 위해 사용
			MultipartFile mf = pet.getPetAttach();
			if (mf == null) {
				throw new IOException("파일이 제대로 첨부되지 않았습니다");
			}
			if (mf != null && !mf.isEmpty()) {
				// 파일 이름 저장
				pet.setPetAttachOname(mf.getOriginalFilename());
				// 파일 타입 저장(png, jpeg)
				pet.setPetAttachType(mf.getContentType());
				// 파일 데이터 저장
				pet.setPetAttachData(mf.getBytes());

				petImageDao.insertPetImage(pet);
			}

			// db에 저장된 pet 정보 조회
			Pet dbPet = petDao.selectByPetId(pet.getPetId());
			// petImage 테이블에서 petId로 이미지 정보 조회
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

	// userId로 회원 정보 조회
	public User info(Integer userId) {
		User user = userDao.selectByUserId(userId);
		if (user == null)
			throw new NoSuchElementException("해당 유저는 존재하지 않습니다");
		return user;
	}

	// 수정 사항 중에 예외 발생 방지 위해 transactional 적용
	@Transactional
	public User update(User user) {
		// db에 존재하는 회원인지 확인
		User dbUser = userDao.selectByUserId(user.getUserId());

		// 존재하지 않으면 null 반환
		if (dbUser == null) {
			throw new NoSuchElementException("해당 유저는 존재하지 않습니다");
		}
		// 전달된 내용이 비어있지 않으면 갱신
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

		// 데이터베이스에 업데이트
		userDao.update(dbUser);
		return dbUser;
	}

	// 가독성, 유지보수 위해 enum 사용
	// 교수님이 하신 방식 사용
	public enum RemoveResult {
		SUCCESS, FAIL
	}

	@Transactional
	public User remove(Integer userId) {
		User user = userDao.selectByUserId(userId);
		if (user == null) {
			throw new NoSuchElementException("해당 유저는 존재하지 않습니다");
		}
		// 삭제된 행 수 반환
		int userRows = userDao.delete(userId);
		// 0이면 실패, 1이상이면 성공
		if (userRows == 0) {
			throw new NoSuchElementException("해당 유저는 존재하지 않습니다");
		} else {
			return user;
		}
	}
}
