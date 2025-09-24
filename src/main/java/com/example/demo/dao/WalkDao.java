package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Walk;

@Mapper
public interface WalkDao {

  // 사용자 아이디로 산책 내역 불러오기
  public List<Walk> selectAllWalkByUserId(Integer userId);
  // 사용자 아이디로 산책 신청받은 내역 불러오기
  public List<Walk> selectAllWalkApplyByUserId(Integer userId);
  
  // 산책 신청
  public int insertWalkApply(Walk walk);
  // 산책 신청 상태 변경
  public int updateWalkApply(Walk walk);
  
  // 산책 기록 추가
  public int insertWalk(Walk walk);
  // 산책 기록 수정
  public int updateWalk(Walk walk);
  // 산책 기록 삭제
  public int deleteWalk(Integer requestOneId);
}
