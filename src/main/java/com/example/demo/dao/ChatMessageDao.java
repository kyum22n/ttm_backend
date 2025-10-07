package com.example.demo.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.dto.ChatMessage;

@Mapper
public interface ChatMessageDao {
  int insert(ChatMessage msg);

  // 히스토리: beforeMessageId 미만의 최근 limit개 (desc)
  List<ChatMessage> selectRecent(
    @Param("roomId") int roomId,
    @Param("beforeMessageId") Integer beforeMessageId,
    @Param("limit") int limit
  );

  List<ChatMessage> selectAfter(
    @Param("roomId") int roomId, 
    @Param("afterMessageId") int afterMessageId
  );


  // 읽음 처리: 해당 사용자가 아닌 상대가 보낸 메시지 중 upTo 이하를 Y로
  int markReadUpTo(
    @Param("roomId") int roomId,
    @Param("readerId") int readerId,
    @Param("upToMessageId") int upToMessageId
  );

  // 안읽음 카운트 (reader 입장에서)
  int countUnread(
    @Param("roomId") int roomId,
    @Param("readerId") int readerId
  );
}