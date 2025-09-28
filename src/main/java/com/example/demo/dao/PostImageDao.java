package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Post;

@Mapper
public interface PostImageDao {
  // 이미지 등록
  int insert(Post image);

  // 이미지 하나 선택
  Post selectById(Integer postImageId);
  
  // 특정 게시물의 이미지 리스트 조회
  List<Post> selectByPostId(Integer postId);

  // 이미지 하나 삭제
  int deleteById(Integer postImageId);
  
  // 특정 게시물의 이미지 삭제
  int deleteByPostId(Integer postId);
}
