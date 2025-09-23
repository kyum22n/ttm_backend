package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Tag;

@Mapper
public interface TagDao {
    public List<Tag> selectAllTag();
    public Tag selectTagByTagId(Integer tagId);
}
