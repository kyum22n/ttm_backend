package com.example.demo.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Post {
	// 게시물 기본 정보	
	private Integer postId;	// 게시물 식별 번호
	private String postTitle;	// 게시물 제목
	private String postContent;	// 게시물 내용
	private Integer likeCount;	// 게시물 좋아요 수
	private String postTag;	// 게시물 태그
	
	// 첨부 이미지
	private Integer imageId; // 0919 imageId 필드가 없으면 바인딩예외 에러 발생
	private MultipartFile postAttach;	// 게시물 이미지
	private String postAttachOname;	 // 게시물 이미지 파일명
	private String postAttachType;	// 게시물 이미지 파일 타입
	private byte[] postAttachData;	// 게시물 이미지 바이너리 데이터
	
	// 산책 모집 글
	private enum isRequest{
		Y, N
	};	// 산책 모집 글 여부
	private LocalDateTime walkStartedAt;	// 산책 시작 시간
	private LocalDateTime walkEndedAt;	// 산책 종료 시간
	private LocalDateTime wApplyEndAt;	// 산책 모집 마감

	
	
	
	
}
