package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.PostTag;
import com.example.demo.dto.Tag;

@Mapper
public interface PostTagDao {
    // 게시물-태그 매핑 추가
    public int insert(PostTag postTag);   

    // 특정 게시물의 태그 정보 가져오기
    public List<Tag> selectTagByPostId(Integer postId);
    
    // 태그 삭제
    public int deleteTagByPostId(PostTag postTag);
}
