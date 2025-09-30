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
    public ResponseEntity<Map<String, Object>> petLike(@RequestParam("userId") Integer userId,
            @RequestParam("petId") Integer petId) {
        Map<String, Object> map = new HashMap<>();

        int rows = likeService.createPetLike(userId, petId);
        map.put("result", "success");
        map.put("rows", rows);
        return ResponseEntity.ok(map);
    }

    // 좋아요 등록(Post)
    @PostMapping("/post-like")
    public ResponseEntity<Map<String, Object>> postLike(@RequestParam("userId") Integer userId,
            @RequestParam("postId") Integer postId) {
        Map<String, Object> map = new HashMap<>();

        int rows = likeService.createPostLike(userId, postId);
        map.put("result", "success");
        map.put("rows", rows);
        return ResponseEntity.ok(map);

    }

    // // 좋아요 취소(Pet)
    @DeleteMapping("/pet-like/cancel")
    public ResponseEntity<Map<String, Object>> petLikeCancel(@RequestParam("userId") Integer userId,
            @RequestParam("petId") Integer petId) {
        Map<String, Object> map = new HashMap<>();

        int rows = likeService.removePetLike(userId, petId);
        map.put("result", "success");
        map.put("rows", rows);
        return ResponseEntity.ok(map);

    }

    // // 좋아요 취소(Post)
    @DeleteMapping("/post-like/cancel")
    public ResponseEntity<Map<String, Object>> postLikeCancel(@RequestParam("userId") Integer userId,
            @RequestParam("postId") Integer postId) {
        Map<String, Object> map = new HashMap<>();

        int rows = likeService.removePostLike(userId, postId);
        map.put("result", "success");
        map.put("rows", rows);
        return ResponseEntity.ok(map);
    }

}
