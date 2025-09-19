package com.example.demo.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Post {
	// 게시글 기본 정보	
	private Integer postId;
	private String postTitle;
	private String postContent;
	private Integer likeCount;
	private String postTag;
	
	// 첨부 이미지
	private Integer imageId; // 0919 imageId 필드가 없으면 바인딩예외 에러 발생
	private MultipartFile postAttach;
	private String postAttachOname;
	private String postAttachType;
	private byte[] postAttachData;
	
	// 산책 모집 글
	private Enum isRequest;
	private Date walkStartedAt;
	private Date walkEndedAt;
	private Date applyEnd;

	// 댓글
	private String commentContent;
	
	
	
	
}
