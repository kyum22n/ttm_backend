// com/example/demo/dto/ChatSendReq.java
package com.example.demo.dto;

import lombok.Data;

@Data
public class ChatSendReq {
  private Integer roomId;     // 기존 방에 보낼 때
  private Integer senderId;
  private String message;
  // roomId가 없고 아래 두 값이 있으면 (상대와) 1:1 방 보장 후 전송
  private Integer otherUserId;
}