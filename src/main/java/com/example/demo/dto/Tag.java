package com.example.demo.dto;

import lombok.Data;

@Data
public class Tag {
    // 게시물 태그
    private Integer tagId;  // 태그 식별 번호
    private String tagName; // 태그 이름
}
