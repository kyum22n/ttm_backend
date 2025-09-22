package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Pager;
import com.example.demo.dto.Post;

@Mapper
public interface PostDao {
  public Post selectByPostId(Integer postId);
  public List<Post> selectAllPostByUserId(Integer userId);
  public int insertPost(Post post);
  public int insertPostImage(Post post);
  public int update(Post post);
  public int delete(Integer postId);
  public int updatePostLikecount(Integer postId);

  // Pager
  public List<Post> selectByPage(Pager pager);
  public int countAll();
  public int countAllByUserId(Integer userId);
}
