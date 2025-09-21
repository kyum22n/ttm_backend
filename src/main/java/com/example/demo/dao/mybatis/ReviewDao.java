package com.example.demo.dao.mybatis;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Review;

@Mapper
public interface ReviewDao {
  // public Review selectAllByReviewId();
  public Review selectByReviewId(Integer reviewId);

  // Review는 받은 사람 id에 맞는거만 한 번에 불러올 수 있으면 될듯
  // 작성받은 사람 id로 받은 리뷰 모두 불러오기
  public Review selectAllByTargetId(Integer targetId);
  // 모집글에 종속된 리뷰 모두 불러오기
  public Review selectAllByPostId(Integer rPostId);

  public int insert(Review review);
  public int update(Review review);
  public int delete(Integer reviewId);
}
