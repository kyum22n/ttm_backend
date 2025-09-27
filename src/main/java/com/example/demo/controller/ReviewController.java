package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Review;
import com.example.demo.service.ReviewService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/review")
@Slf4j
public class ReviewController {
  @Autowired
  private ReviewService reviewService;

  // 리뷰 쓰기
  @PostMapping("/write")
  public Map<String, Object> reviewWrite(@RequestBody Review review) {
    log.info(review.toString());

    Map<String, Object> map = new HashMap<>();
    String result = reviewService.create(review);

    if("fail".equals(result)) {
      map.put("result", "fail");
      map.put("message", "리뷰 등록 실패");

    } else {
      map.put("result", "success");
      map.put("review", review);
    }

    return map;
  }

  // 해당 유저가 받은 리뷰 모두 가져오기
  @GetMapping("/users/{userId}/reviews")
  public List<Review> getReceivedReviews(@PathVariable("userId") int userId) {
    log.info("userId: {}", userId);

    return reviewService.findAllByTargetId(userId);
  }

  // 특정 리뷰 하나만 가져오기
  @GetMapping("/detail")
  public Review getReview(@RequestParam("reviewId") int reviewId) {
    return reviewService.findOneByReviewId(reviewId);
  }
}

