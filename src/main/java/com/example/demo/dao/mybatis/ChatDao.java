package com.example.demo.dao.mybatis;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Chat;

@Mapper
public interface ChatDao {
  public int insert(Chat chat);
  public int update(Chat chat);
  public int delete(int chatroomId);
  public Chat selectByChatId(int chatroomId);
}
