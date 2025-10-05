package com.example.demo.dto;

import lombok.Data;

@Data
public class ChatHistoryReq {
  private Integer roomId;
  private Integer limit = 50;        // 최근 N개
  private Integer beforeMessageId;      // 페이징(이 ID 미만)
}