package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.Review;

@Mapper
public interface ReviewDao {
  public Review selectByReviewId(@Param("reviewId") Integer reviewId);

  // Review는 받은 사람 id에 맞는거만 한 번에 불러올 수 있으면 될듯
  // 작성받은 사람 id로 받은 리뷰 모두 불러오기
  public List<Review> selectAllByTargetId(@Param("targetId") Integer targetId);
  // 모집글에 종속된 리뷰 모두 불러오기
  // public List<Review> selectAllByPostId(@Param ("rPostId") Integer rPostId);

  public int insert(Review review);
  // public int update(Review review); 아무리 생각해도 필요 없음 ㅋㅋ
  // public int delete(Integer reviewId);
}
