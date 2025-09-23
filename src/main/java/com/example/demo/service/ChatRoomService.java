package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dao.ChatRoomDao;
import com.example.demo.dto.ChatRoom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomDao chatRoomDao;

    // 특정 유저의 모든 채팅방 조회
    public List<ChatRoom> getRoomsByUserId(Integer userId) {
        return chatRoomDao.selectAllByUserId(userId);
    }

    // 특정 채팅방 조회
    public ChatRoom getRoomById(Integer chatroomId) {
        return chatRoomDao.selectByChatRoomId(chatroomId);
    }

    // 채팅방 생성
    public int createRoom(ChatRoom chatRoom) {
        return chatRoomDao.insert(chatRoom);
    }

    // 채팅방 수정
    public int updateRoom(ChatRoom chatRoom) {
        return chatRoomDao.update(chatRoom);
    }

    // 채팅방 삭제
    public int deleteRoom(Integer chatroomId) {
        return chatRoomDao.delete(chatroomId);
    }
}