package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Pager;
import com.example.demo.dto.Post;

@Mapper
public interface PostDao {

  // 게시물 식별 번호로 게시물 조회
  public Post selectByPostId(Integer postId);
  // 특정 사용자의 모든 게시물 조회
  public List<Post> selectAllPostByUserId(Integer userId);

  // 게시물 작성
  public int insertPost(Post post);
  // 게시물 수정
  public int updatePost(Post post);
  // 게시물 삭제
  public int deletePost(Integer postId);

  // 게시물 좋아요 수 증가
  public int increasePostLikecount(Integer postId);
  // 게시물 좋아요 수 감소
  public int decreasePostLikecount(Integer postId);

  //////////////////////////////////////////////////////////////

  // 그룹 산책 모집글 목록 조회
  public List<Post> selectAllGroupWalkPost(String isRequest);
  // 그룹 산책 완료된 글 목록 조회
  public List<Post> selectAllEndedGroupWalk();
  // 그룹 산책 완료된 글 하나 조회
  public Post selectEndedGroupWalk(Integer postId);

  
  public int markWalkStartedNow(Integer postId);
  public int markWalkEndedNow(Integer postId);
  public int markWApplyEndedNow(Integer postId);

  // 합치기 (합친 후엔 위에거 삭제 가능)
  public int markWalkByCode(@Param("postId") int postId, @Param("code") int code);
  
  // Pager
  public List<Post> selectByPage(Pager pager);
  public int countAll();
  public int countAllByUserId(Integer userId);
}
