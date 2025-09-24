package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.PostDao;
import com.example.demo.dto.Pager;
import com.example.demo.dto.Post;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostService {
  @Autowired
  private PostDao postDao;
  
  // 게시물 작성
  public void writePost(Post post) {
    postDao.insertPost(post);

    if (post.getPostAttachData() != null && post.getPostAttachData().length > 0) {
      log.info("이미지 저장 시도: {}", post.getPostAttachOname());
      postDao.insertPostImage(post);
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

  // 게시물 수정
  public int modifyPost(Post post) {
    int rows = postDao.updatePost(post);
    if (post.getPostAttachData() != null && post.getPostAttachData().length > 0) {
      postDao.updatePostImage(post);
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
    Post post = postDao.selectByPostId(postId);
    
    if(post.getPostAttachData() != null && post.getPostAttachData().length > 0) {
      postDao.deletePostImage(postId);
    }

    int rows = postDao.deletePost(postId);

    return rows;
  }


  // 여기서부터 GroupWalk 점령지
  // 산책 지원(participates 등록)
  public void applyGroupWalk(Interger ) {

  }

}
