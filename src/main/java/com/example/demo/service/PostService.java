package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dao.ParticipateDao;
import com.example.demo.dao.PostDao;
import com.example.demo.dao.PostImageDao;
import com.example.demo.dto.Pager;
import com.example.demo.dto.Post;
import com.example.demo.dto.PostImage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostService {
  @Autowired
  private PostDao postDao;
  @Autowired
  private PostImageDao postImageDao;
  @Autowired
  private ParticipateDao participateDao;

  // 게시물 작성
  public Post write(Post post) throws Exception {
    // 1) 글 저장 (postId 채워짐)
    postDao.insertPost(post);

    // 2) 이미지 저장 (단일 + 다중)
    if (post.getPostAttach() != null && !post.getPostAttach().isEmpty()) {
      PostImage img = new PostImage();
      img.setPostId(post.getPostId());
      img.setPostAttachOname(post.getPostAttach().getOriginalFilename());
      img.setPostAttachType(post.getPostAttach().getContentType());
      img.setPostAttachData(post.getPostAttach().getBytes());
      postImageDao.insert(img);
    }
    List<MultipartFile> files = post.getPostAttaches();
    if (files != null) {
      for (MultipartFile mf : files) {
        if (mf != null && !mf.isEmpty()) {
          PostImage img = new PostImage();
          img.setPostId(post.getPostId());
          img.setPostAttachOname(mf.getOriginalFilename());
          img.setPostAttachType(mf.getContentType());
          img.setPostAttachData(mf.getBytes());
          postImageDao.insert(img);
        }
      }
    }

    // 3) 요청글이면 작성자 자동 신청(P) 후 승인(A)
    if ("Y".equalsIgnoreCase(String.valueOf(post.getIsRequest()))) {
      Integer pid = post.getPostId();
      Integer uid = post.getPostUserId();
      if (pid == null || uid == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "postId/postUserId 누락");
      }
      if (participateDao.exists(pid, uid) == 0) {
        participateDao.insert(pid, uid, "P");
      }
      participateDao.updateStatus(pid, uid, "A");
    }

    // 4) 최신 글 반환
    return postDao.selectByPostId(post.getPostId());
  }

  // 전체 게시물 목록 불러오기(페이지)
  public List<Post> getPostListByPage(Pager pager) {
    List<Post> list = postDao.selectByPage(pager);
    return list;
  }

  // 페이지의 전체 행 수 불러오기
  public int getTotalRows() {
    int totalRows = postDao.countAll();
    return totalRows;
  }

  // 특정 사용자 게시물 목록 불러오기(페이지)
  public List<Post> getPostListByUserId(Integer userId) {
    List<Post> list = postDao.selectAllPostByUserId(userId);
    return list;
  }

  // 특정 사용자 아이디를 이용해 전체 행 수 불러오기
  public int getTotalRowsByUserId(Integer userId) {
    int totalRows = postDao.countAllByUserId(userId);
    return totalRows;
  }

  // 게시물 상세보기
  public Post postDetail(Integer postId) {
    Post post = postDao.selectByPostId(postId);
    return post;
  }

  // 이미지 조회 보조 메서드
  public List<PostImage> getImagesByPostId(Integer postId) {
    List<PostImage> images = postImageDao.selectByPostId(postId);
    return images != null ? images : new ArrayList<>();
  }

  // 게시물 수정
  public int modifyPostWithImages(Post post, String imageMode, boolean clearImages) throws Exception {
    int rows = postDao.updatePost(post);

    // 새 파일 수집
    List<MultipartFile> files = new ArrayList<>();
    if (post.getPostAttach() != null && !post.getPostAttach().isEmpty())
      files.add(post.getPostAttach());
    if (post.getPostAttaches() != null) {
      for (var mf : post.getPostAttaches())
        if (mf != null && !mf.isEmpty())
          files.add(mf);
    }
    boolean hasNewFiles = !files.isEmpty();

    // 1) 명시 전체삭제
    if (clearImages) {
      postImageDao.deleteByPostId(post.getPostId());
    }

    // 2) 교체 모드면(그리고 새 파일이 있으면) 기존 이미지 삭제 후 교체
    if (!clearImages && "replace".equalsIgnoreCase(imageMode) && hasNewFiles) {
      postImageDao.deleteByPostId(post.getPostId());
    }

    // 3) 새 파일 저장 (append면 기존 유지 + 추가, replace면 위에서 삭제 후 재삽입)
    if (hasNewFiles) {
      for (var mf : files) {
        PostImage img = new PostImage();
        img.setPostId(post.getPostId());
        img.setPostAttachOname(mf.getOriginalFilename());
        img.setPostAttachType(mf.getContentType());
        img.setPostAttachData(mf.getBytes());
        postImageDao.insert(img);
      }
    }

    return rows;
  }

  // 좋아요 수 증가
  public void increasePostLikecount(Integer postId) {
    postDao.increasePostLikecount(postId);
  }

  // 좋아요 취소
  public void decreasePostLikecount(Integer postId) {
    postDao.decreasePostLikecount(postId);
  }

  // 게시물 삭제
  public int removePost(Integer postId) {
    postImageDao.deleteByPostId(postId);
    return postDao.deletePost(postId);
  }

  // //////////////////////////////////////////////////////

  // 그룹 산책 모집글만 조회

  
  // 그룹 산책 완료된 글만 조회

  // 산책 상태 변경 및 시간 입력
  public Post markWApplyEndedNow(int postId) {
    int rows = postDao.markWApplyEndedNow(postId);
    if (rows == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조건 불충족(모집글 아님/이미 마감)");
    return postDao.selectByPostId(postId);
  }

  public Post markWalkStartedNow(int postId) {
    int rows = postDao.markWalkStartedNow(postId);
    if (rows == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조건 불충족(마감 없음/이미 시작)");
    return postDao.selectByPostId(postId);
  }

  public Post markWalkEndedNow(int postId) {
    int rows = postDao.markWalkEndedNow(postId);
    if (rows == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조건 불충족(시작 없음/이미 종료)");
    return postDao.selectByPostId(postId);
  }

}
