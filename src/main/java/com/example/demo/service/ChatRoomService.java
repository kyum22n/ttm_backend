package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.ChatRoomDao;
import com.example.demo.dto.ChatRoom;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatRoomService {
    
    @Autowired
    private ChatRoomDao chatRoomDao;

    public ChatRoom getById(int roomId) {
    return chatRoomDao.selectById(roomId);
  }

  // 방이 존재하지 않으면 생성, 아니면 방의 정보를 반환
  // 기존 방의 조건 : 두 유저의 아이디가 동일하고 상태가 P나 A인것
  @Transactional
  public ChatRoom ensurePairRoom(int userA, int userB, int requestedBy) {
    ChatRoom found = chatRoomDao.selectPairRoom(userA, userB);
    if (found != null) return found;

    ChatRoom room = new ChatRoom();
    room.setChatuser1Id(userA);
    room.setChatuser2Id(userB);
    room.setRequestedBy(requestedBy);
    room.setChatroomStatus("P"); // 최초 요청 상태
    chatRoomDao.insert(room);
    // 필요 시 승인 전환 로직(자동 승인 원하면 아래 사용)
    chatRoomDao.updateStatus(room.getChatroomId(), "A");
    room.setChatroomStatus("A");
    return chatRoomDao.selectPairRoom(userA, userB); // 재조회
  }

  public boolean isMember(int roomId, int userId) {
    ChatRoom r = chatRoomDao.selectById(roomId);
    if (r == null) return false;
    return userId == r.getChatuser1Id() || userId == r.getChatuser2Id();
  }

}