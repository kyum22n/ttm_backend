package com.example.demo.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ChatRoom {
  private Integer chatroomId;
  private Integer chatuser1Id;
  private Integer chatuser2Id;
  private Integer requestedBy;
  private String chatroomStatus; // P/A/B/D
  private LocalDateTime updatedAt;
  private LocalDateTime createdAt;
}
