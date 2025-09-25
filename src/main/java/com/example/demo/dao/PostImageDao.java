package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.PostImage;

@Mapper
public interface PostImageDao {
  int insert(PostImage image);

  // 단건/리스트 조회
  PostImage selectById(Integer postImageId);
  List<PostImage> selectByPostId(Integer postId);

  // 삭제
  int deleteById(Integer postImageId);
  int deleteByPostId(Integer postId);
}
