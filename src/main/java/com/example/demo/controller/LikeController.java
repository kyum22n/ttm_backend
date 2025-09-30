package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.LikeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/like")
@Slf4j
public class LikeController {
    @Autowired
    private LikeService likeService;

    // 좋아요 등록(Pet)
    @PostMapping("/pet-like")
    public int petLike(@RequestParam("userId") Integer userId,
            @RequestParam("petId") Integer petId) {
        return likeService.createPetLike(userId, petId);
    }

    // 좋아요 등록(Post)
    @PostMapping("/post-like")
    public int postLike(@RequestParam("userId") Integer userId,
            @RequestParam("postId") Integer postId) {
        return likeService.createPostLike(userId, postId);

    }

    // // 좋아요 취소(Pet)
    @DeleteMapping("/pet-like/cancel")
    public int petLikeCancel(@RequestParam("userId") Integer userId,
            @RequestParam("petId") Integer petId) {
        return likeService.removePetLike(userId, petId);

    }

    // // 좋아요 취소(Post)
    @DeleteMapping("/post-like/cancel")
    public int postLikeCancel(@RequestParam("userId") Integer userId,
            @RequestParam("postId") Integer postId) {
        return likeService.removePostLike(userId, postId);
    }

}
