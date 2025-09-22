package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.ChatMessage;

@Mapper
public interface ChatMessageDao {
  public ChatMessage selectByChatId(Integer messageId);
  public int insert(ChatMessage chatMessage);
}
