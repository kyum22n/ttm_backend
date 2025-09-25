package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  // 게시물 작성
  public void writePost(Post post) throws Exception {
    postDao.insertPost(post); // postId 채워짐

    // 1) 기존 단일 파일도 지원
    if (post.getPostAttach() != null && !post.getPostAttach().isEmpty()) {
      PostImage img = new PostImage();
      img.setPostId(post.getPostId());
      img.setPostAttachOname(post.getPostAttach().getOriginalFilename());
      img.setPostAttachType(post.getPostAttach().getContentType());
      img.setPostAttachData(post.getPostAttach().getBytes());
      postImageDao.insert(img);
    }

    // 2) ✅ 다중 파일 처리
    if (post.getPostAttaches() != null && !post.getPostAttaches().isEmpty()) {
      for (var mf : post.getPostAttaches()) {
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

  // ✅ 이미지 조회 보조 메서드
  public List<PostImage> getImagesByPostId(Integer postId) {
    List<PostImage> images = postImageDao.selectByPostId(postId);
    return images != null ? images : new ArrayList<>();
  }

  // 게시물 수정
  public int modifyPost(Post post) throws Exception {
    int rows = postDao.updatePost(post);

    // 수정 시 이미지 교체 전략 예시:
    // - 간단히: 기존 전부 삭제 후 새로 넣기
    if (post.getPostAttaches() != null && !post.getPostAttaches().isEmpty()) {
      postImageDao.deleteByPostId(post.getPostId());
      for (var mf : post.getPostAttaches()) {
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
}
