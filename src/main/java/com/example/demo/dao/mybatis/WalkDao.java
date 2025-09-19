package com.example.demo.dao.mybatis;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Walk;

@Mapper
public interface WalkDao {
  public int insert(Walk walk);
  public int update(Walk walk);
  public int delete();
  public Walk selectByWalkId();
}
