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
}
