package com.example.demo.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.WalkDao;
import com.example.demo.dto.Walk;

@Service
public class WalkService {

    @Autowired
    private WalkDao walkDao;

    public List<Walk> getWalkListByUserId(Integer userId) {
        return walkDao.selectAllWalkByUserId(userId);
    }

    public List<Walk> getWalkApplyListByReceiveUserId(Integer receiveUserId) {
        return walkDao.selectAllWalkApplyByReceiveUserId(receiveUserId);
    }

    public List<Walk> getWalkApplyListByRequestUserId(Integer requestUserId) {
        return walkDao.selectAllWalkApplyByRequestUserId(requestUserId);
    }

    @Transactional
    public void createWalkApply(Walk walk) {
        walk.setRstatus("P");
        walkDao.insertWalkApply(walk);
    }

    @Transactional
    public int modifyWalkApplyStatus(Integer requestOneId, String rstatus, Integer receiveUserId) {
        String normalized = "A".equalsIgnoreCase(rstatus) ? "A"
                         : "R".equalsIgnoreCase(rstatus) ? "R"
                         : "P";
        return walkDao.updateWalkApplyStatus(requestOneId, normalized, receiveUserId);
    }

    /* ============================
       ✅ 동시성 안전: 산책 시작
       - 조건부 UPDATE로 1건만 성공
       - 이미 시작 상태면 기존 시각을 반환(멱등)
       ============================ */
    @Transactional
    public Timestamp startOneOnOne(Integer requestOneId, Integer userId) {
        int updated = walkDao.updateWalkStartedAt(requestOneId, userId);
        if (updated == 1) {
            return walkDao.selectWalkStartedAt(requestOneId);
        }
        Timestamp started = walkDao.selectWalkStartedAt(requestOneId);
        if (started != null) {
            return started; // 이미 시작됨
        }
        throw new IllegalStateException("시작할 수 없는 상태입니다.");
    }

    /* ============================
       ✅ 동시성 안전: 산책 종료
       - 조건부 UPDATE로 1건만 성공
       - 이미 종료 상태면 기존 시각을 반환(멱등)
       ============================ */
    @Transactional
    public Timestamp endOneOnOne(Integer requestOneId, Integer userId) {
        int updated = walkDao.updateWalkEndedAt(requestOneId, userId);
        if (updated == 1) {
            return walkDao.selectWalkEndedAt(requestOneId);
        }
        Timestamp ended = walkDao.selectWalkEndedAt(requestOneId);
        if (ended != null) {
            return ended; // 이미 종료됨
        }
        throw new IllegalStateException("종료할 수 없는 상태입니다.");
    }

    // (호환용) 기존 int 반환 시그니처 유지해야 하면 아래처럼 래핑해도 됨
    @Transactional
    public int modifyWalkStartedAt(Integer requestOneId, Integer userId) {
        return (startOneOnOne(requestOneId, userId) != null) ? 1 : 0;
    }

    @Transactional
    public int modifyWalkEndedAt(Integer requestOneId, Integer userId) {
        return (endOneOnOne(requestOneId, userId) != null) ? 1 : 0;
    }

    public Walk getEndedWalk(Integer requestOneId) {
        return walkDao.selectEndedWalkByRequestOneId(requestOneId);
    }

    @Transactional
    public int removeWalk(Integer requestOneId, Integer userId) {
        int rows = walkDao.deleteWalk(requestOneId, userId);
        if (rows == 0) throw new NoSuchElementException();
        return rows;
    }
}
