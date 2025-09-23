package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.TagDao;
import com.example.demo.dto.Tag;

@Service
public class TagService {
    @Autowired
    private TagDao tagDao;

    // 모든 태그 조회
    public List<Tag> getAllTags() {
        List<Tag> list = tagDao.selectAllTag();
        return list;
    }

    // 태그 하나 선택
    public Tag getTag(Integer tagId) {
        Tag tag = tagDao.selectTagByTagId(tagId);
        return tag;
    }

}
