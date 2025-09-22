package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.CommentDao;
import com.example.demo.dto.Comment;

@Service
public class CommentService {
  @Autowired
  private CommentDao commentDao;

  // 특정 게시물의 전체 댓글 불러오기
  public List<Comment> getCommentListByPostId(Integer cpostId) {
    List<Comment> list = commentDao.selectAllCommentByPostId(cpostId);
    return list;
  }

  // 특정 댓글 불러오기
  public Comment getCommentByCommentId(Integer commentId) {
    Comment comment = commentDao.selectCommentByCommentId(commentId);
    return comment;
  }

  // 댓글 작성
  public void writeComment(Comment comment) {
    commentDao.insert(comment);
  }

  // 댓글 수정
  public Comment modifyComment(Comment comment) {
    commentDao.update(comment);
    Comment dbComment = commentDao.selectCommentByCommentId(comment.getCommentId());
    return dbComment;
  }

  // 댓글 삭제
  public int deleteComment(Integer commentId) {
    int rows = commentDao.delete(commentId);
    return rows;
  }
}
