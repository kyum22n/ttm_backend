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

    Review result = reviewService.create(review);

    Map<String, Object> map = new HashMap<>();
    map.put("result", "success");
    map.put("review", review);

    return map;
  }

  // 해당 유저가 받은 리뷰 모두 가져오기 (성공 시 200)
  @GetMapping("/users/{userId}/reviews")
  public ResponseEntity<Map<String, String>> getReceivedReviews(@PathVariable("userId") int userId) {
    log.info("userId: {}", userId);

    var reviews = reviewService.findAllByTargetId(userId); // 실패는 전역 예외처리로 감

    Map<String, String> body = new HashMap<>();
    body.put("result", "success");
    body.put("message", reviews.isEmpty() ? "받은 리뷰가 없습니다." : "받은 리뷰 목록을 조회했습니다.");
    body.put("count", String.valueOf(reviews.size())); // 데이터 자체는 안 담고 개수만 전달

    return ResponseEntity.ok(body);
  }

  // 특정 리뷰 하나만 가져오기 (성공 시 200)
  // 전역 예외처리: 못 찾으면 NoSuchElementException → 404로 응답됨
  @GetMapping("/detail")
  public ResponseEntity<Map<String, String>> getReview(@RequestParam("reviewId") int reviewId) {
    var review = reviewService.findOneByReviewId(reviewId);

    Map<String, String> body = new HashMap<>();
    body.put("result", "success");
    body.put("message", "리뷰를 조회했습니다.");
    body.put("reviewId", String.valueOf(review.getReviewId())); // 핵심 키만 문자열로 전달
    // 필요하면 추가로 writerId/targetId 등도 문자열로 넣을 수 있음:
    // body.put("writerId", String.valueOf(review.getWriterId()));
    // body.put("targetId", String.valueOf(review.getTargetId()));

    return ResponseEntity.ok(body);
  }
}
