package com.example.demo.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.User;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private JavaMailSender mailSender;

    // loginId를 통해 user 객체 받아오기(로그인/비밀번호 찾기용)
    public User getUserByLoginId(String userLoginId) {
        User user = userDao.selectUserByLoginId(userLoginId);
        return user;
    }

    // userId를 통해 user 객체 받아오기(pk 기준)
    public User getUserById(Integer userId) {
        User user = userDao.selectByUserId(userId);
        return user;
    }

    // email을 통해 user 객체 조회(아이디 찾기용)
    public User getUserByEmail(String userEmail) {
        User user = userDao.selectUserByEmail(userEmail);
        return user;
    }

    // 임시 비밀번호 발급
    public void sendTempPassword(User user) {
        // 1. 임시 비밀번호 생성
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        // 2. 임시 비밀번호 암호화
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(tempPassword);

        // 3. DB에 암호화된 비밀번호 저장
        user.setUserPassword(encodedPassword); // 암호화된 비밀번호 저장
        userDao.updatePassword(user);

        // 4. 이메일 전송(원본 임시 비밀번호는 메일로만 전달)
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getUserEmail());
        message.setSubject("[서비스명] 임시 비밀번호 안내");
        message.setText(
                "안녕하세요, " + user.getUserName() + "님.\n\n" +
                        "요청하신 임시 비밀번호는 [" + tempPassword + "] 입니다.\n" +
                        "로그인 후 반드시 비밀번호를 변경해주세요.");

        mailSender.send(message);
    }

}
