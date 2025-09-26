package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.LikeDao;
import com.example.demo.dao.PetDao;
import com.example.demo.dao.PostDao;
import com.example.demo.dto.Like;

@Service
public class LikeService {
    @Autowired
    private LikeDao likeDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PetDao petDao;

    @Transactional
    // 좋아요 등록(Pet)
    public String createPetLike(Integer userId, Integer petId) throws Exception {
        int checkRows = likeDao.selectLikeFromPet(userId, petId);
        if(checkRows == 0) {
            likeDao.insertLikeToPet(userId, petId);
            petDao.increasePetLikecount(petId);
            return "success";
        } else {
            return "이미 누른 좋아요";
        }
    }
    
    @Transactional
    // 좋아요 등록(Post)
    public String createPostLike(Integer userId, Integer postId) throws Exception {
        int checkRows = likeDao.selectLikeFromPost(userId, postId);

        if(checkRows == 0) {
            likeDao.insertLikeToPost(userId, postId);
            postDao.increasePostLikecount(postId);
            return "success";
        } else {
            return "이미 누른 좋아요";
        }

    }
    
    @Transactional
    // 좋아요 취소(Pet)
    public int removePetLike(Integer userId, Integer petId) throws Exception {
        int rows = likeDao.deleteLikeFromPet(userId, petId);
        if(rows > 0) {
            petDao.decreasePetLikecount(petId);
        }

        return rows;
    }
    
    @Transactional
    // 좋아요 취소(Post)
    public int removePostLike(Integer userId, Integer postId) throws Exception {
        int rows = likeDao.deleteLikeFromPost(userId, postId);
        if(rows > 0) {
            postDao.decreasePostLikecount(postId);
        }
        
        return rows;
    }
}
