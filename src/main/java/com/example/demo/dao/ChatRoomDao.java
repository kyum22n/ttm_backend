package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.ChatRoom;

@Mapper
public interface ChatRoomDao {
  ChatRoom selectById(@Param("roomId") int roomId);

  // 두 유저 조합의 방(승인상태 A 우선), 없으면 NULL
  ChatRoom selectPairRoom(@Param("u1") int u1, @Param("u2") int u2);

  int insert(ChatRoom room);

  int updateStatus(@Param("roomId") int roomId, @Param("status") String status);

  // 존재하지 않으면 생성(P) → 서비스에서 A로 전환 가능
}