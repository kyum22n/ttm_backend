package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dao.ParticipateDao;
import com.example.demo.dto.Participate;
import com.example.demo.enums.ParticipateStatus;

@Service
@Transactional
public class ParticipateService {

  @Autowired
  private ParticipateDao participateDao;

  /** status 기반 단일 처리 (P=INSERT, A/R/C=UPDATE, CANCEL=DELETE) */
  public void handleByStatus(Participate participate, ParticipateStatus status) {
    final int postId = participate.getPostId();
    final int userId = participate.getUserId();

    final int exists = participateDao.exists(postId, userId);

    if (status == ParticipateStatus.P) {
      // 신청 INSERT (중복 방지)
      if (exists > 0) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신청된 사용자입니다.");
      }
      int rows = participateDao.insert(postId, userId, status.getDbStatus()); // "P"
      if (rows != 1) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "신청 처리에 실패했습니다.");
      }
      return;
    }

    // 나머지 상태들은 기존 레코드가 필요
    if (status.isRequireExisting() && exists <= 0) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "신청 내역이 없습니다.");
    }

    if (status.isDeleteOp()) {
      // CANCEL -> DELETE
      int rows = participateDao.delete(postId, userId);
      if (rows != 1) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "취소 처리에 실패했습니다. 다시 시도해주세요.");
      }
    } else {
      // A/R/C -> UPDATE
      int rows = participateDao.updateStatus(postId, userId, status.getDbStatus()); // "A","R","C"
      if (rows != 1) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "상태 변경에 실패했습니다. 다시 시도해주세요.");
      }
    }
  }

  /** 해당 post의 대기(P) 목록 */
  public List<Participate> listPendingByPost(int postId) {
    return participateDao.findByPostAndStatus(postId, "P");
  }

  /** 해당 post의 승인(A) 목록 */
  public List<Participate> listApprovedByPost(int postId) {
    return participateDao.findByPostAndStatus(postId, "A");
  }

  /** 글 기준 전체 목록 */
  public List<Participate> listByPost(int postId) {
    return participateDao.findByPost(postId);
  }

  /** 유저 기준 전체 목록 */
  public List<Participate> listByUser(int userId) {
    return participateDao.findByUser(userId);
  }

  /** 승인 인원수 (null-safe) */
  public int countApproved(int postId) {
    Integer c = participateDao.countByPostAndStatus(postId, "A");
    return c == null ? 0 : c;
  }
}