package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;


@Data
public class Post {
	// 게시물 기본 정보	
	// FK
	private Integer postUserId;	// 게시물 작성자
	//
	private Integer postId;	// 게시물 식별 번호
	private String postTitle;	// 게시물 제목
	private String postContent;	// 게시물 내용
	private Integer postLikeCount;	// 게시물 좋아요 수
	private LocalDateTime createdAt;	// 게시물 등록일
	private LocalDateTime updatedAt; // 게시물 수정일
	
	// 첨부 이미지
	private Integer postImageId; // 0919 imageId 필드가 없으면 바인딩예외 에러 발생
	private MultipartFile postAttach;	// 게시물 이미지
	private String postAttachOname;	 // 게시물 이미지 파일명
	private String postAttachType;	// 게시물 이미지 파일 타입
	private byte[] postAttachData;	// 게시물 이미지 바이너리 데이터

	// 다중 업로드용
  	private List<MultipartFile> postAttaches; // form-data name: "postAttaches"
	
	// 산책 모집 글
	private String isRequest;	// 산책 모집 글 여부
	private LocalDateTime walkStartedAt;	// 산책 시작 시간
	private LocalDateTime walkEndedAt;	// 산책 종료 시간
	private LocalDateTime wApplyEndedAt;	// 산책 모집 마감
	
}
