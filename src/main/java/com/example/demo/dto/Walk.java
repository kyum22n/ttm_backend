package com.example.demo.dto;

import java.time.LocalDateTime;

public class Walk {
	// 산책 신청자
	private Integer participate;
	// 산책 모집글
	private Integer postId;
	// 산책 신청 처리 상태
	private enum requestStatus{
		P, A, R, C
	};
	// 산책 시작 시간
	private LocalDateTime walkStartedAt;
	// 산책 종료 시간
	private LocalDateTime walkEndedAt;
}
