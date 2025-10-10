package com.example.demo.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.LikeDao;
import com.example.demo.dao.PetDao;
import com.example.demo.dao.PostDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LikeService {
    @Autowired
    private LikeDao likeDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PetDao petDao;

    // 좋아요 등록/취소(Pet)
    @Transactional
    public boolean togglePetLike(Integer userId, Integer petId) {

        // 현재 좋아요 여부 조회
        int exists = likeDao.selectLikeFromPet(userId, petId);

        if (exists > 0) {
            // 이미 좋아요 → 취소
            likeDao.deleteLikeFromPet(userId, petId);
            petDao.decreasePetLikecount(petId);
            return false;
        } else {
            // 아직 안 눌렀음 → 등록
            likeDao.insertLikeToPet(userId, petId);
            petDao.increasePetLikecount(petId);
            return true;
        }
    }

    // 좋아요 등록/취소(Post)
    @Transactional
    public boolean togglePostLike(Integer userId, Integer postId) {
        // 1️. 게시글 작성자 확인
        Integer postOwnerId = postDao.selectUserIdByPostId(postId);
        if (postOwnerId != null && postOwnerId.equals(userId)) {
            throw new IllegalArgumentException("자신의 게시글에는 좋아요를 누를 수 없습니다.");
        }

        // 2️. 중복 체크
        int exists = likeDao.selectLikeFromPost(userId, postId);

        if (exists > 0) {
            likeDao.deleteLikeFromPost(userId, postId);
            postDao.decreasePostLikecount(postId);
            return false;
        } else {
            likeDao.insertLikeToPost(userId, postId);
            postDao.increasePostLikecount(postId);
            return true;
        }
    }

    /** 좋아요 상태 확인(post) */
    public boolean isPostLiked(Integer userId, Integer postId) {
        return likeDao.selectLikeFromPost(userId, postId) > 0;
    }

    public boolean isPetLiked(Integer userId, Integer petId){
        return likeDao.selectLikeFromPet(userId, petId) > 0;
    }
}
