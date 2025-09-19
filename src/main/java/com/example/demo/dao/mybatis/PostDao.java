package com.example.demo.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Pager;
import com.example.demo.dto.Post;

@Mapper
public interface PostDao {
  public int insert(Post post);
  public int update(Post post);
  public int delete(Integer postId);
  public Post selectByPostId(Integer postId);
  public List<Post> selectByPage(Pager pager);
  public int countAll();
}
