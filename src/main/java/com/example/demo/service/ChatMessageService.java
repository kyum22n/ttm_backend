package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dao.ChatMessageDao;
import com.example.demo.dto.ChatMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageDao chatMessageDao;

    // 메시지 저장
    public void save(ChatMessage message) {
        chatMessageDao.insert(message);
    }

    // 특정 채팅방 메시지 목록 조회
    public List<ChatMessage> getMessages(int chatroomId) {
        return chatMessageDao.selectAllByRoomId(chatroomId);
    }
}
