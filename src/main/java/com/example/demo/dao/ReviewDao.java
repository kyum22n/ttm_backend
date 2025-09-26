package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.Review;

@Mapper
public interface ReviewDao {
  public Review selectByReviewId(@Param("reviewId") Integer reviewId);

  // 작성받은 사람 id로 받은 리뷰 모두 불러오기
  public List<Review> selectAllByTargetId(@Param("targetId") Integer targetId);

  // 모집글에 종속된 리뷰 모두 불러오기
  // public List<Review> selectAllByPostId(@Param ("rPostId") Integer rPostId);

  public int insert(Review review);
}
