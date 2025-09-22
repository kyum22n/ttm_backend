package com.example.demo.dao;

import com.example.demo.dto.Comment;

public interface CommentDao {
  // 댓글
  public int insert(Comment comment);
  public int update(Comment comment);
  public int delete(Integer commentId);
}
