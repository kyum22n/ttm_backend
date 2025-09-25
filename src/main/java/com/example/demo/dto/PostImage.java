package com.example.demo.dto;

import lombok.Data;

@Data
public class PostImage {
  private Integer postImageId;
  private Integer postId;
  private String  postAttachOname;
  private String  postAttachType;
  private byte[]  postAttachData;
}