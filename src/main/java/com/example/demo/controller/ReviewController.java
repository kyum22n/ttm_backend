package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    Review reviewSaved = reviewService.create(review);

    Map<String, Object> map = new HashMap<>();
    map.put("result", "success");
    map.put("review", reviewSaved);

    return map;
  }

  // 해당 유저가 받은 리뷰 모두 가져오기 (성공 시 200)
  @GetMapping("/users/{userId}/reviews")
  public ResponseEntity<Map<String, Object>> getReceivedReviews(@PathVariable("userId") int userId) {
    List<Review> reviews = reviewService.findAllByTargetId(userId);

    Map<String, Object> map = new HashMap<>();
    map.put("result", "success");
    map.put("message", reviews.isEmpty() ? "받은 리뷰가 없습니다." : "받은 리뷰 목록을 조회했습니다.");
    map.put("count", reviews.size()); // 숫자 그대로
    map.put("data", reviews); // ★ 실제 목록 포함

    return ResponseEntity.ok(map);
  }

  // 특정 리뷰 하나만 가져오기 (성공 시 200)
  // 전역 예외처리: 못 찾으면 NoSuchElementException → 404로 응답됨
  @GetMapping("/detail")
  public ResponseEntity<Map<String, String>> getReview(@RequestParam("reviewId") int reviewId) {
    var review = reviewService.findOneByReviewId(reviewId);

    Map<String, String> body = new HashMap<>();
    body.put("result", "success");
    body.put("message", "리뷰를 조회했습니다.");
    body.put("reviewId", String.valueOf(review.getReviewId()));

    return ResponseEntity.ok(body);
  }
}
