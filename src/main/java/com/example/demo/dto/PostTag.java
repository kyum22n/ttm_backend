package com.example.demo.dto;

import lombok.Data;

@Data
public class PostTag {
    private Integer postId; // 게시물 식별 번호
    private Integer tagId;  // 태그 식별 번호
    private String tagName; // 태그 이름
}
