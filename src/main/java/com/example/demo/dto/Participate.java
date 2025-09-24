package com.example.demo.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participate {
  /** 복합 PK 일부: 어떤 글에 대한 신청/참가인지 */
  private int postId;

  /** 복합 PK 일부: 어떤 유저가 신청/참가했는지 */
  private int userId;

  /** 신청/참가 상태: 'P' 대기, 'A' 승인, 'R' 거절, 'C' 완료 */
  private String applyStatus;

  /** 생성 시각 (DB: TIMESTAMP(6)) */
  private LocalDateTime createdAt;

  /** 최근 변경 시각 (DB: TIMESTAMP(6)) */
  private LocalDateTime updatedAt;
}