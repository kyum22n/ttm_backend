package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.PostDao;
import com.example.demo.dao.ReviewDao;
import com.example.demo.dao.WalkDao;
import com.example.demo.dto.Post;
import com.example.demo.dto.Review;
import com.example.demo.dto.Walk;

@Service
public class ReviewService {
  @Autowired
  private ReviewDao reviewDao;

  @Autowired
  private PostDao postDao;

  @Autowired
  private WalkDao walkDao;

  // 산책 리뷰 작성
  @Transactional
  public Review create(Review review) {
    // 입력 유효성 검증
    if (review == null) {
      throw new IllegalArgumentException("리뷰 데이터가 없습니다");
    }
    if (review.getWriterId() == null || review.getTargetId() == null) {
      throw new IllegalArgumentException("writerId와 targetId는 필수입니다");
    }
    if (review.getWriterId().equals(review.getTargetId())) {
      throw new IllegalArgumentException("본인에게 리뷰를 작성할 수 없습니다");
    }
    if (review.getPostId() == null && review.getRequestOneId() == null) {
      throw new IllegalArgumentException("postId와 requestOneId 중 하나는 필수입니다.");
    }

    Post endedGroupWalk = null;
    Walk endedWalk = null;

    if (review.getPostId() != null) {
      endedGroupWalk = postDao.selectEndedGroupWalk(review.getPostId());
    }

    if (review.getRequestOneId() != null) {
      endedWalk = walkDao.selectEndedWalkByRequestOneId(review.getRequestOneId());
    }

    // 그룹 산책 완료된 건 또는 1:1 산책 완료된 건만 리뷰 등록 성공
    if (endedGroupWalk == null && endedWalk == null) {
      throw new IllegalStateException("산책이 완료된 건에만 리뷰를 작성 할 수 있습니다.");
    }

    reviewDao.insert(review);

    return reviewDao.selectByReviewId(review.getReviewId());
  }

  // 자신이 받은 리뷰 모두 불러오기 (예외 X, 빈 리스트 반환)
  public List<Review> findAllByTargetId(int userId) {
    if (userId <= 0)
      throw new IllegalArgumentException("userId가 올바르지 않습니다");
    List<Review> list = reviewDao.selectAllByTargetId(userId);
    return (list != null) ? list : java.util.Collections.emptyList();
  }

  // // 단건 조회 혹시 몰라서
  // public Review findOneByReviewId(int reviewId) {
  // return reviewDao.selectByReviewId(reviewId);
  // }

  // 단건 조회 (없으면 404용 예외)
  public Review findOneByReviewId(int reviewId) {
    if (reviewId <= 0)
      throw new IllegalArgumentException("reviewId가 올바르지 않습니다");
    Review review = reviewDao.selectByReviewId(reviewId);
    if (review == null)
      throw new NoSuchElementException("리뷰를 찾을 수 없습니다: reviewId=" + reviewId);
    return review;

  }
}
