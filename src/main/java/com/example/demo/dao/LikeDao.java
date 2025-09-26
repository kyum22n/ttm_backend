package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeDao {

    // 펫 좋아요 중복 체크
    public int selectLikeFromPet(@Param("userId") Integer userId, @Param("petId") Integer petId);
    // 게시물 좋아요 중복 체크
    public int selectLikeFromPost(@Param("userId") Integer userId, @Param("postId") Integer postId);

    // 좋아요 등록
    public int insertLikeToPet(@Param("userId") Integer userId, @Param("petId") Integer petId);
    public int insertLikeToPost(@Param("userId") Integer userId, @Param("postId") Integer postId);

    // 좋아요 취소
    public int deleteLikeFromPet(@Param("userId") Integer userId, @Param("petId") Integer petId);
    public int deleteLikeFromPost(@Param("userId") Integer userId, @Param("postId") Integer postId);

}
