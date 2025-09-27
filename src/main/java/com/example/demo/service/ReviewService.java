package com.example.demo.service;

import java.util.List;

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
  public String create(Review review) {

    Post endedGroupWalk = null;
    Walk endedWalk = null;

    if(review.getPostId() != null) {
      endedGroupWalk = postDao.selectEndedGroupWalk(review.getPostId());
    }

    if(review.getRequestOneId() != null) {
      endedWalk = walkDao.selectEndedWalkByRequestOneId(review.getRequestOneId());
    }

    // 그룹 산책 완료된 건 또는 1:1 산책 완료된 건만 리뷰 등록 성공
    if(endedGroupWalk == null && endedWalk == null) {
      return "fail";
      
    }
    reviewDao.insert(review);
    return "success";
  }

  // 자신이 받은 리뷰 모두 불러오기
  public List<Review> findAllByTargetId(int userId) {
    return reviewDao.selectAllByTargetId(userId);
  }

  // 단건 조회 혹시 몰라서
  public Review findOneByReviewId(int reviewId) {
    return reviewDao.selectByReviewId(reviewId);
  }
}
