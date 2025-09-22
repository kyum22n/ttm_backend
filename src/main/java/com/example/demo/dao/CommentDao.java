package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Comment;

@Mapper
public interface CommentDao {
  // 댓글
  public List<Comment> selectAllCommentByPostId(Integer cpostId);
  public Comment selectCommentByCommentId(Integer commentId);
  public int insert(Comment comment);
  public int update(Comment comment);
  public int delete(Integer commentId);
}
