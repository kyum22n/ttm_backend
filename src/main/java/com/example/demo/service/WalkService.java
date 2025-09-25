package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.WalkDao;
import com.example.demo.dto.Walk;

@Service
public class WalkService {
    @Autowired
    private WalkDao walkDao;

    // 사용자 아이디로 1:1 산책 내역 불러오기
    public List<Walk> getWalkListByUserId(Integer userId) {
        List<Walk> walkList = walkDao.selectAllWalkByUserId(userId);
        return walkList;
    }

    // 사용자 아이디로 1:1 산책 신청받은 내역 불러오기
    public List<Walk> getWalkApplyListByReceiveUserId(Integer receiveUserId) {
        List<Walk> walkReceiveList = walkDao.selectAllWalkApplyByReceiveUserId(receiveUserId);
        return walkReceiveList;
    }

    // 사용자 아이디로 1:1 산책 신청한 내역 불러오기
    public List<Walk> getWalkApplyListByRequestUserId(Integer requestUserId) {
        List<Walk> walkRequestList = walkDao.selectAllWalkApplyByRequestUserId(requestUserId);
        return walkRequestList;
    }

    // 1:1 산책 신청
    public void createWalkApply(Walk walk) {
        walk.setRstatus("P");
        walkDao.insertWalkApply(walk);
    }

    // 1:1 산책 신청 상태 변경
    public int modifyWalkApplyStatus(Integer requestOneId, String rstatus, Integer receiveUserId) {
        Walk walk = new Walk();
        walk.setRequestOneId(requestOneId);
        walk.setReceiveUserId(receiveUserId);

        switch(rstatus) {
            case("A"): 
                walk.setRstatus("A");   // 승인(Accept)
                break;
            case("R"):
                walk.setRstatus("R");   // 거절(Reject)
                break;
            default:
                walk.setRstatus("P");    // 대기(Pending)
        }

        int rows = walkDao.updateWalkApplyStatus(requestOneId, rstatus, receiveUserId);

        return rows;
    }

    // 1:1 산책 시작
    public int modifyWalkStartedAt(Integer requestOneId, Integer userId) {
        int rows = walkDao.updateWalkStartedAt(requestOneId, userId);
        return rows;
    }

    // 1:1 산책 종료
    public int modifyWalkEndedAt(Integer requestOneId, Integer userId) {
        int rows = walkDao.updateWalkEndedAt(requestOneId, userId);
        return rows;
    }

    // 1:1 산책 기록 삭제
    public int removeWalk(Integer requestOneId, Integer userId) {
        int rows = walkDao.deleteWalk(requestOneId, userId);
        return rows;
    }

}
