package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.ChatRoom;

@Mapper
public interface ChatRoomDao {
  ChatRoom selectById(@Param("roomId") int roomId);

  /** 상태 IN ('P','A') 로 두 유저(정렬된) 방 조회 */
  ChatRoom selectPairRoom(@Param("u1") int u1, @Param("u2") int u2);

  void insert(ChatRoom room);

  void updateStatus(@Param("roomId") int roomId, @Param("status") String status);
}
