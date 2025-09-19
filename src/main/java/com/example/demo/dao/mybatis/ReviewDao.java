package com.example.demo.dao.mybatis;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Review;

@Mapper
public interface ReviewDao {
  public Review selectAllByReviewId();
  public Review selectByReviewId(Integer reviewId);
  public int insert(Review review);
  public int update(Review review);
  public int delete(Integer reviewId);
}
