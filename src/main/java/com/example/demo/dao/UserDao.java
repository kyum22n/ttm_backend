package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.User;

@Mapper
public interface UserDao {
  public List<User> selectAllUser();
  public User selectByUserId(Integer userId);
  public int insert(User user);
  public int update(User user);
  public int delete(Integer userId);

  public User selectUserByLoginId(String userLoginId); //로그인 정보를 받기 위한 메서드 추가
  public User selectUserByEmail(String userEmail); // 아이디 찾기를 하기 위한 메서드 추가
  public int updatePassword(User user); //임시 비밀번호 발급을 위한 메서드 추가
}
