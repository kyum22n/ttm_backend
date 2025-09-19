package com.example.demo.dao.mybatis;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.ChatMessage;

@Mapper
public interface ChatMessageDao {
  public ChatMessage selectByChatId(Integer messageId);
  public int insert(ChatMessage chatMessage);
}
