package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.PostDao;
import com.example.demo.dao.ReviewDao;
import com.example.demo.dao.WalkDao;
import com.example.demo.dto.Review;

@Service
public class ReviewService {
  @Autowired
  private ReviewDao reviewDao;

  @Autowired
  private PostDao postDao;

  @Autowired
  private WalkDao walkDao;

  public Review create(Review review) {
    // 그룹 산책 완료된 건만 가져오기

    // 1:1 산책 완료된 건만 가져오기
    walkDao.selectAllWalkByUserId(review.getWriterId());

    // 산책 완료된 건만 리뷰 등록 성공
    reviewDao.insert(review);

    return review;
  }

  public List<Review> findAllByTargetId(int userId) {
    return reviewDao.selectAllByTargetId(userId);
  }

  // 단건 조회 혹시 몰라서
  public Review findOneByReviewId(int reviewId) {
    return reviewDao.selectByReviewId(reviewId);
  }
}
