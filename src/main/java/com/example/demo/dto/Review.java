package com.example.demo.dto;

import lombok.Data;

@Data
public class Review {
  private Integer reviewId;
  private Integer postId;        // rPostId → postId
  private Integer requestOneId;
  private Integer writerId;      // rWriterId → writerId
  private Integer targetId;
  private Integer reviewTagId;
}