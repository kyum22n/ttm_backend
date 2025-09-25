package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeDao {

    // 펫 좋아요 중복 체크
    public int selectLikeFromPet(Integer userId, Integer petId);
    // 게시물 좋아요 중복 체크
    public int selectLikeFromPost(Integer userId, Integer postId);

    // 좋아요 등록
    public int insertLikeToPet(Integer petId);
    public int insertLikeToPost(Integer postId);

    // 좋아요 취소
    public int deleteLikeFromPet(Integer petId);
    public int deleteLikeFromPost(Integer postId);
}
