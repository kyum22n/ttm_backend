package com.example.demo.dao.mybatis;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.ChatRoom;

@Mapper
public interface ChatRoomDao {
  public ChatRoom selectAllByChatRoomId();
  public ChatRoom selectByChatRoomId(Integer chatroomId);
  public int insert(ChatRoom chatRoom);
  public int update(ChatRoom chatRoom);
  public int delete(Integer chatroomId);
}
