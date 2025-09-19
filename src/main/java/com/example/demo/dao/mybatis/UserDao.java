package com.example.demo.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.User;

@Mapper
public interface UserDao {
  public List<User> selectByAllUser();
  public User selectByUserId(Integer userId);
  public int insert(User user);
  public int update(User user);
  public int delete(Integer userId);
}
