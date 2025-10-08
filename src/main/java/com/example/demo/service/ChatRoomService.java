package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.ChatRoomDao;
import com.example.demo.dto.ChatRoom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ChatRoomDao chatRoomDao;

  public ChatRoom getById(int roomId) {
    return chatRoomDao.selectById(roomId);
  }

  @Transactional
  public ChatRoom ensurePairRoom(int userA, int userB, int requestedBy) {
    int u1 = Math.min(userA, userB);
    int u2 = Math.max(userA, userB);

    ChatRoom found = chatRoomDao.selectPairRoom(u1, u2); // P/A 조회
    if (found != null)
      return found;

    ChatRoom room = new ChatRoom();
    room.setChatuser1Id(u1);
    room.setChatuser2Id(u2);
    room.setRequestedBy(requestedBy);
    room.setChatroomStatus("P"); // 최초 P
    chatRoomDao.insert(room);
    return chatRoomDao.selectPairRoom(u1, u2);
  }

  public boolean isMember(int roomId, int userId) {
    ChatRoom r = chatRoomDao.selectById(roomId);
    return r != null && (userId == r.getChatuser1Id() || userId == r.getChatuser2Id());
  }

  public boolean isApproved(int roomId) {
    ChatRoom r = chatRoomDao.selectById(roomId);
    return r != null && "A".equals(r.getChatroomStatus());
  }

  @Transactional
  public void approve(int roomId, int byUserId) {
    ChatRoom r = chatRoomDao.selectById(roomId);
    if (r == null)
      throw new RuntimeException("NOT_FOUND");
    if (!(byUserId == r.getChatuser1Id() || byUserId == r.getChatuser2Id()))
      throw new IllegalArgumentException("NOT_MEMBER");
    chatRoomDao.updateStatus(roomId, "A");
  }

  @Transactional
  public void reject(int roomId, int byUserId) {
    ChatRoom r = chatRoomDao.selectById(roomId);
    if (r == null)
      throw new RuntimeException("NOT_FOUND");
    if (!(byUserId == r.getChatuser1Id() || byUserId == r.getChatuser2Id()))
      throw new IllegalArgumentException("NOT_MEMBER");
    chatRoomDao.updateStatus(roomId, "D");
  }

  // 내가 참여중인 채팅방 목록 가져오기
  public List<ChatRoom> findRoomsByUser(int userId) {
    return chatRoomDao.selectRoomsByUser(userId);
  }
}
