package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.ChatMessage;

@Mapper
public interface ChatMessageDao {
  // 메시지 단건 조회
  public ChatMessage selectByChatId(Integer messageId);

  // 특정 채팅방의 모든 메시지 조회
  public List<ChatMessage> selectAllByRoomId(Integer chatroomId);

  // 메시지 저장
  public int insert(ChatMessage chatMessage);

  // 메시지 읽음 처리
  public int updateReadStatus(Integer messageId);

  // 특정 유저가 보낸 메시지 전체 조회 (옵션)
  public List<ChatMessage> selectAllBySenderId(Integer senderId);
}