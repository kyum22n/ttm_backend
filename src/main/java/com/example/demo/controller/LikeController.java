package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.LikeService;
import com.example.demo.service.PetService;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/like")
@Slf4j
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Autowired
    private PostService postService;

    // 좋아요 등록(Pet)
    @PostMapping("/pet-like")
    public Map<String, Object> petLike(@RequestParam("userId") Integer userId,
                                       @RequestParam("petId") Integer petId) throws Exception {
        Map<String, Object> map = new HashMap<>();

        String result = likeService.createPetLike(userId, petId);

        switch(result) {
            case("success"):
                map.put("result", "success");
                break;
            case("이미 누른 좋아요"):
                map.put("result", "fail");
                map.put("message", "좋아요 등록 실패");
                break;
            default:
                map.put("result", "server error");
            }
            
            return map;
    }
        
    // 좋아요 등록(Post)
    @PostMapping("/post-like")
    public Map<String, Object> postLike(@RequestParam("userId") Integer userId,
                                        @RequestParam("postId") Integer postId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        
        String result = likeService.createPostLike(userId, postId);
        
        if(result == "success") {
            map.put("result", "success");

        } else if(result == "이미 누른 좋아요") {
            map.put("result", "fail");
            map.put("message", "좋아요 등록 실패");

        } else {
            map.put("result", "server error");
            
        }
        
        return map;

    }
    
    
    // // 좋아요 취소(Pet)
    @DeleteMapping("/pet-like/cancel")
    public Map<String, Object> petLikeCancel(@RequestParam("userId") Integer userId,
                                             @RequestParam("petId") Integer petId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        
        int rows = likeService.removePetLike(userId, petId);
        
        if(rows > 0) {
            map.put("result", "success");
        } else {
            map.put("result", "fail");
        }
        
        return map;
        
    }
    
    // // 좋아요 취소(Post)
    @DeleteMapping("/post-like/cancel")
    public Map<String, Object> postLikeCancel(@RequestParam("userId") Integer userId,
                                              @RequestParam("postId") Integer postId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        
        int rows = likeService.removePostLike(userId, postId);
        
        if(rows > 0) {
            map.put("result", "success");
        } else {
            map.put("result", "fail");
        }
        
        return map;
        
    }
    
}
