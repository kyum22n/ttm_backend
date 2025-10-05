package com.example.demo.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.ChatMessageDao;
import com.example.demo.dto.ChatMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatMessageService {
    @Autowired
    private ChatMessageDao chatMessageDao;

    @Transactional
  public ChatMessage send(int roomId, int senderId, String text) {
    ChatMessage m = new ChatMessage();
    m.setChatroomId(roomId);
    m.setSenderId(senderId);
    m.setMessage(text);
    chatMessageDao.insert(m);
    return m;
  }

    public List<ChatMessage> recent(int roomId, int beforeMessageId, int limit) {
    List<ChatMessage> list = chatMessageDao.selectRecent(roomId, beforeMessageId, limit);
    // selectRecent는 DESC로 가져오므로, 프론트 편의를 위해 시간순 정렬로 반환
    Collections.reverse(list);
    return list;
  }

  @Transactional
  public int markRead(int roomId, int readerId, int upToMessageId) {
    return chatMessageDao.markReadUpTo(roomId, readerId, upToMessageId);
  }

  public int unreadCount(int roomId, int readerId) {
    return chatMessageDao.countUnread(roomId, readerId);
  }


}
