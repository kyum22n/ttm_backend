package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Like {
    private Integer likeId;
    private Integer userId;
    private Integer petId;
    private Integer postId;
    private LocalDateTime createdAt;
}
