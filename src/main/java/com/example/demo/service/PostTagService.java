package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.PostTagDao;
import com.example.demo.dto.PostTag;
import com.example.demo.dto.Tag;

@Service
public class PostTagService {
    @Autowired
    private PostTagDao postTagDao;

    // 게시물에 태그 달기
    public void taging(PostTag postTag) {
        postTagDao.insert(postTag);
    }

    // 게시물 조회 시 태그도 같이 조회
    public List<Tag> getTagNames(Integer postId) {
        List<Tag> tags = postTagDao.selectTagByPostId(postId);
        return tags;
    }

    // 태그 삭제
    public void removeTag(PostTag postTag) {
        postTagDao.deleteTagByPostId(postTag);
    }
}
