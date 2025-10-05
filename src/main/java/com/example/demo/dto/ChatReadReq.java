package com.example.demo.dto;

import lombok.Data;

@Data
public class ChatReadReq {
  private Integer roomId;
  private Integer userId;        // 읽는 사람
  private Integer upToMessageId; // 이 ID 이하를 읽음 처리
}