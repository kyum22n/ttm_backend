package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.ChatMessage;

@Mapper
public interface ChatMessageDao {

  List<ChatMessage> selectHistory(@Param("roomId") int roomId,
                                  @Param("beforeMessageId") Integer beforeMessageId,
                                  @Param("limit") int limit);

  void insert(ChatMessage msg);

  int markRead(@Param("roomId") int roomId,
               @Param("userId") int userId,
               @Param("upToMessageId") int upToMessageId);

  int unreadCount(@Param("roomId") int roomId,
                  @Param("userId") int userId);
}
