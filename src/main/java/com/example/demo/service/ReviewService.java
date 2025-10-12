package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
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
    if (review == null)
      throw new IllegalArgumentException("리뷰 데이터가 없습니다");
    if (review.getWriterId() == null || review.getTargetId() == null)
      throw new IllegalArgumentException("writerId와 targetId는 필수입니다");
    if (review.getWriterId().equals(review.getTargetId()))
      throw new IllegalArgumentException("본인에게 리뷰를 작성할 수 없습니다");

    // ✅ XOR 검사: postId 와 requestOneId 중 '정확히 하나'만 허용
    boolean hasPost = review.getPostId() != null;
    boolean hasWone = review.getRequestOneId() != null;
    if (hasPost == hasWone) // 둘 다 true 또는 둘 다 false
      throw new IllegalArgumentException("postId와 requestOneId 중 정확히 하나만 설정해야 합니다.");

    Post endedGroupWalk = null;
    Walk endedWalk = null;

    if (hasPost) {
      endedGroupWalk = postDao.selectEndedGroupWalk(review.getPostId());
    } else {
      endedWalk = walkDao.selectEndedWalkByRequestOneId(review.getRequestOneId());
    }

    if (endedGroupWalk == null && endedWalk == null)
      throw new IllegalStateException("산책이 완료된 건에만 리뷰를 작성 할 수 있습니다.");

    reviewDao.insert(review);
    return reviewDao.selectByReviewId(review.getReviewId());
  }

  // 해당 유저가 받은 리뷰 모두 불러오기 (예외 X, DAO 반환 그대로)
  public List<Review> findAllByTargetId(int userId) {
    if (userId <= 0) {
      throw new IllegalArgumentException("userId가 올바르지 않습니다");
    }
    List<Review> list = reviewDao.selectAllByTargetId(userId);
    return list; // 빈 리스트/NULL 여부는 DAO 구현에 따름
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
