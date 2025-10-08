package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    // 좋아요 등록/취소(Pet)
    @PostMapping("/pet-like")
    public ResponseEntity<Map<String, Object>> togglePetLike(
            @RequestParam("userId") Integer userId,
            @RequestParam("petId") Integer petId) {

        boolean liked = likeService.togglePetLike(userId, petId);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        map.put("liked", liked);
        return ResponseEntity.ok(map);
    }

    // 좋아요 등록/취소(Post)
    @PostMapping("/post-like")
    public ResponseEntity<Map<String, Object>> togglePostLike(
            @RequestParam("userId") Integer userId,
            @RequestParam("postId") Integer postId) {

        boolean liked = likeService.togglePostLike(userId, postId);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        map.put("liked", liked);
        return ResponseEntity.ok(map);
    }

    // 특정 게시물에 대해 좋아요 상태 확인
    @GetMapping("/post-like/status")
    public ResponseEntity<Map<String, Object>> getPostLikeStatus(
            @RequestParam("userId") Integer userId,
            @RequestParam("postId") Integer postId) {

        boolean isLiked = likeService.isPostLiked(userId, postId);

        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        map.put("isLiked", isLiked);
        return ResponseEntity.ok(map);
    }

}
