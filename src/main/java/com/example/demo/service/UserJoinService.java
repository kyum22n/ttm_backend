package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.User;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserJoinService {
	@Autowired
	private UserDao userDao;

	public void join(User user) {
		int rows = userDao.insert(user);
		if (rows == 0) {
			log.error("회원가입에 실패했습니다.", user);
		} else {
			log.info("회원가입에 성공했습니다.", user);
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
			// null, 빈문자열, 공백이 아니면 수정
			// 이것도 그냥 validation으로 처리하는게 맞는거 같은데..
			if (StringUtils.isNotBlank(user.getUserName())) {
				dbUser.setUserName(user.getUserName());
			}
			if (StringUtils.isNotBlank(user.getUserEmail())) {
				dbUser.setUserEmail(user.getUserEmail());
			}
			if (StringUtils.isNotBlank(user.getUserPassword())) {
				dbUser.setUserPassword(user.getUserPassword());
			}
			if (StringUtils.isNotBlank(user.getUserAddress())) {
				dbUser.setUserAddress(user.getUserAddress());
			}
			// Date는 비문자열이기 때문에 null 체크
			// 근데 이것도 바뀔 이유가 없지 않나...?
			if (user.getUserBirthDate() != null) {
				dbUser.setUserBirthDate(user.getUserBirthDate());
			}
			// createdAt은 회원가입일이므로 수정하지 않음 xml에서도 수정해야 하나..?
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
}

// 반려동물 등록할때 uid 널허용
