package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.Pager;
import com.example.demo.dto.Post;

@Mapper
public interface PostDao {
  public Post selectByPostId(Integer postId);
  public List<Post> selectAllPostByUserId(Integer userId);

  public int insertPost(Post post);
  // public int insertPostImage(Post post); 분리!
  public int updatePost(Post post);
  public int deletePost(Integer postId);
  // public int updatePostImage(Post post);
  public int increasePostLikecount(Integer postId);
  public int decreasePostLikecount(Integer postId);

  
  int markWalkStartedNow(Integer postId);
  int markWalkEndedNow(Integer postId);
  int markWApplyEndedNow(Integer postId);
  

  // Pager
  public List<Post> selectByPage(Pager pager);
  public int countAll();
  public int countAllByUserId(Integer userId);
}
