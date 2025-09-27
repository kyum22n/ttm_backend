package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.Walk;

@Mapper
public interface WalkDao {

  // 사용자 아이디로 완료된 1:1 산책 목록 불러오기
  public List<Walk> selectAllWalkByUserId(Integer userId);
  // 사용자 아이디로 1:1 산책 신청받은 내역 불러오기
  public List<Walk> selectAllWalkApplyByReceiveUserId(Integer receiveUserId);
  // 사용자 아이디로 1:1 산책 신청한 내역 불러오기
  public List<Walk> selectAllWalkApplyByRequestUserId(Integer requestUserId);
  
  // 1:1 산책 신청
  public int insertWalkApply(Walk walk);
  // 1:1 산책 신청 상태 변경
  public int updateWalkApplyStatus(@Param("requestOneId") Integer requestOneId, 
  @Param("rstatus") String rstatus, 
  @Param("receiveUserId") Integer receiveUserId);
  
  // 1:1 산책 시작
  public int updateWalkStartedAt(@Param("requestOneId") Integer requestOneId, @Param("userId") Integer userId);
  // 1:1 산책 종료
  public int updateWalkEndedAt(@Param("requestOneId")Integer requestOneId, @Param("userId")Integer userId);
  // 1:1 산책 식별 번호로 완료된 내역 불러오기
  public Walk selectEndedWalkByRequestOneId(Integer requestOneId);
  // 1:1 산책 기록 삭제
  public int deleteWalk(@Param("requestOneId")Integer requestOneId, @Param("userId")Integer userId);
}
