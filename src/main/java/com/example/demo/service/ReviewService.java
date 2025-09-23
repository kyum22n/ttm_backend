package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ReviewDao;
import com.example.demo.dto.Review;

@Service
public class ReviewService {
  @Autowired
  private ReviewDao reviewDao;

  public Review create(Review review) {
    reviewDao.insert(review);

    return review;
  }

  public List<Review> findAllByTargetId(int userId) {
    return reviewDao.selectAllByTargetId(userId);
  }
}
