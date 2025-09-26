package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dao.ParticipateDao;
import com.example.demo.dto.Participate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipateService {

  private final ParticipateDao participateDao;

  /** 신청(P) – 바로 INSERT */
  public void groupWalkApply(Participate p) {
    participateDao.insert(p.getPostId(), p.getUserId(), "P");
  }

  /** 승인(A) */
  public int groupWalkApprove(int postId, int userId) {
    return participateDao.updateStatus(postId, userId, "A");
  }

  /** 산책 모집 포스트 작성시 작성자 즉시 승인처리할 오버로드 */
  public void groupWalkApproveAuto(Participate p) {
    participateDao.updateStatus(p.getPostId(), p.getUserId(), "A");
  }


  /** 거절(R) */
  public int groupWalkReject(int postId, int userId) {
    return participateDao.updateStatus(postId, userId, "R");
  }

  /** 완료(C) */
  public int groupWalkComplete(int postId, int userId) {
    return participateDao.updateStatus(postId, userId, "C");
  }

  /** 취소(레코드 삭제) */
  public int groupWalkApplyCancel(int postId, int userId) {
    return participateDao.delete(postId, userId);
  }

  /** 해당 post의 대기(P) 목록 */
  public List<Participate> listPendingByPost(int postId) {
    return participateDao.findByPostAndStatus(postId, "P");
  }

  /** 해당 post의 승인(A) 목록 */
  public List<Participate> listApprovedByPost(int postId) {
    return participateDao.findByPostAndStatus(postId, "A");
  }


  
  /** 글 기준 목록 */
  public List<Participate> listByPost(int postId) {
    return participateDao.findByPost(postId);
  }

  /** 유저 기준 목록 */
  public List<Participate> listByUser(int userId) {
    return participateDao.findByUser(userId);
  }

  /** (옵션) 승인 인원수 */
  public int countApproved(int postId) {
    Integer c = participateDao.countByPostAndStatus(postId, "A");
    return c == null ? 0 : c;
  }

}
