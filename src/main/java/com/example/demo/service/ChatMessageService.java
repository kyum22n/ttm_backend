package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.ChatMessageDao;
import com.example.demo.dto.ChatMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageDao chatMessageDao;

  @Transactional(readOnly = true)
  public List<ChatMessage> history(int roomId, Integer beforeMessageId, int limit) {
    return chatMessageDao.selectHistory(roomId, beforeMessageId, limit);
  }

  @Transactional
  public ChatMessage send(int roomId, int senderId, String message) {
    ChatMessage m = new ChatMessage();
    m.setChatroomId(roomId);
    m.setSenderId(senderId);
    m.setMessage(message);
    chatMessageDao.insert(m); // PK는 트리거
    return m;
  }

  @Transactional
  public int markRead(int roomId, int userId, Integer upToMessageId) {
    if (upToMessageId == null) return 0;
    return chatMessageDao.markRead(roomId, userId, upToMessageId);
  }

  @Transactional(readOnly = true)
  public int unreadCount(int roomId, int userId) {
    return chatMessageDao.unreadCount(roomId, userId);
  }
}
