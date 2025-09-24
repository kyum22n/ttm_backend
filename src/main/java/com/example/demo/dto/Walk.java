package com.example.demo.dto;

import java.time.LocalDateTime;

public class Walk {

	// 1:1 산책 신청 건
	private Integer requestOneId;

	// 1:1 산책 신청자
	private Integer requestUserId;
	// 1:1 산책 신청 수락자
	private Integer receiveUserId;
	
	// 1:1 산책 신청 시간
	private LocalDateTime createdAt;
	// 1:1 산책 신청 처리 상태
	private String rStatus;
	
	// 1:1 산책 시작 시간
	private LocalDateTime walkStartedAt;
	// 1:1 산책 종료 시간
	private LocalDateTime walkEndedAt;
}
