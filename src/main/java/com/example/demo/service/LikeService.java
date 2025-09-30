package com.example.demo.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.LikeDao;
import com.example.demo.dao.PetDao;
import com.example.demo.dao.PostDao;

@Service
public class LikeService {
    @Autowired
    private LikeDao likeDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PetDao petDao;

    // 좋아요 등록(Pet)
    @Transactional
    public int createPetLike(Integer userId, Integer petId) {
        int checkRows = likeDao.selectLikeFromPet(userId, petId);
        if (checkRows == 0) {
            likeDao.insertLikeToPet(userId, petId);
            petDao.increasePetLikecount(petId);
            return checkRows;
        } else {
            throw new IllegalArgumentException("이미 누른 좋아요");
        }
    }

    // 좋아요 등록(Post)
    @Transactional
    public int createPostLike(Integer userId, Integer postId) {
        int checkRows = likeDao.selectLikeFromPost(userId, postId);

        if (checkRows == 0) {
            likeDao.insertLikeToPost(userId, postId);
            postDao.increasePostLikecount(postId);
            return checkRows;
        } else {
            throw new IllegalArgumentException("이미 누른 좋아요");
        }

    }

    @Transactional
    // 좋아요 취소(Pet)
    public int removePetLike(Integer userId, Integer petId) {
        int rows = likeDao.deleteLikeFromPet(userId, petId);
        if (rows > 0) {
            petDao.decreasePetLikecount(petId);
        } else {
            throw new NoSuchElementException();
        }

        return rows;
    }

    @Transactional
    // 좋아요 취소(Post)
    public int removePostLike(Integer userId, Integer postId) {
        int rows = likeDao.deleteLikeFromPost(userId, postId);
        if (rows > 0) {
            postDao.decreasePostLikecount(postId);
        } else {
            throw new NoSuchElementException();
        }
        return rows;
    }
}
