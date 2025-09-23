package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.Comment;
import com.example.demo.dto.Pager;
import com.example.demo.dto.Post;
import com.example.demo.dto.PostTag;
import com.example.demo.dto.Tag;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import com.example.demo.service.PostTagService;
import com.example.demo.service.TagService;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {
  // 의존 주입
  @Autowired
  private PostService postService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private TagService tagService;

  @Autowired
  private PostTagService postTagService;

  // 게시물 작성
  @PostMapping("/write")
  public Post postWrite(Post post) throws Exception {
    // 첨부 이미지 파일 변환
    MultipartFile mf = post.getPostAttach();
    if(mf != null && !mf.isEmpty()) {
      log.info("이미지 저장 시도: {}", mf.getOriginalFilename());
      post.setPostAttachOname(mf.getOriginalFilename());
      post.setPostAttachType(mf.getContentType());
      post.setPostAttachData(mf.getBytes());
    }

    // 서비스 호출
    postService.writePost(post);

    // DB에 새로 저장된 값 읽어오기
    Post dbPost = postService.postDetail(post.getPostId());

    return dbPost;
  }
  
  // 전체 게시물 목록 불러오기(페이징)
  @GetMapping("/list")
  public Map<String, Object> postList(@RequestParam(value="pageNo", defaultValue="1") int pageNo) {
    int totalRows = postService.getTotalRows();
    Pager pager = new Pager(10, 5, totalRows, pageNo);

    List<Post> list = postService.getPostListByPage(pager);

    Map<String, Object> map = new HashMap<>();
    map.put("pager", pager);
    map.put("posts", list);
    
    return map;
  }

  // 특정 사용자 게시물 목록 불러오기(페이징)
  @GetMapping("/{userId}/posts")
  public Map<String, Object> userPostList(
    @PathVariable("userId") Integer userId,
    @RequestParam(value="pageNo", defaultValue="1") int pageNo) {
    
    int totalRows = postService.getTotalRowsByUserId(userId);
    Pager pager = new Pager(10, 5, totalRows, pageNo);

    List<Post> list = postService.getPostListByUserId(userId);

    Map<String, Object> map = new HashMap<>();
    map.put("pager", pager);
    map.put("posts", list);

    return map;
  }

  // 게시물 상세보기
  @GetMapping("/detail")
  public Map<String, Object> postDetail(@RequestParam("postId") Integer postId) {
    
    Post post = postService.postDetail(postId);
    List<Comment> comments = commentService.getCommentListByPostId(postId);
    List<Tag> tags = postTagService.getTagNames(postId);

    Map<String, Object> map = new HashMap<>();
    map.put("post", post);
    map.put("comments", comments);
    map.put("tags", tags);

    return map;
  }

  // 게시물 수정
  @PutMapping("/update")
  public Post postUpdate(Post post) throws Exception {

    MultipartFile mf = post.getPostAttach();
    if(mf != null && !mf.isEmpty()) {
      post.setPostAttachOname(mf.getOriginalFilename());
      post.setPostAttachType(mf.getContentType());
      post.setPostAttachData(mf.getBytes());
    }

    postService.modifyPost(post);

    Post dbPost = postService.postDetail(post.getPostId());

    return dbPost;
  }

  // 게시물 좋아요
  @PostMapping("/{postId}/like")
  public void postLike(@PathVariable("postId") Integer postId) {
    postService.increasePostLikecount(postId);      
  }

  // 게시물 좋아요 취소
  @DeleteMapping("/{postId}/like/cancel")
  public void postLikeCancel(@PathVariable("postId") Integer postId) {
    postService.decreasePostLikecount(postId);
  }

  // 게시물 삭제
  @DeleteMapping("/delete")
  public Map<String, String> postDelete(@RequestParam("postId") Integer postId) {
    int rows = postService.removePost(postId);
    
    Map<String, String> map = new HashMap<>();
    if(postId == null && rows < 0) {
      map.put("result", "fail");
    } else {
      map.put("result", "success");
    }

    return map;
  }

  // 댓글 작성
  @PostMapping("/comment-write")
  public void commentWrite(Comment comment) {
    commentService.writeComment(comment);
  }

  // 댓글 수정
  @PutMapping("/comment-update")
  public Comment commentUpdate(Comment comment) {
    commentService.modifyComment(comment);
    
    Comment dbComment = commentService.getCommentByCommentId(comment.getCommentId());

    return dbComment;
  }

  // 댓글 삭제
  @DeleteMapping("/comment-delete")
  public Map<String, String> commentDelete(@RequestParam("commentId") Integer commentId) {
    int rows = commentService.deleteComment(commentId);
    
    Map<String, String> map = new HashMap<>();
    if(commentId == null && rows < 0) {
      map.put("result", "fail");
    } else {
      map.put("result", "success");
    }

    return map;
  }

  // 전체 태그 목록 조회
  @GetMapping("/tags")
  public List<Tag> tagList() {
    List<Tag> tags = tagService.getAllTags();
    return tags;
  }

  // 게시물에 태그 달기
  @PostMapping("/add-tag")
  public void addTag(@RequestBody PostTag postTag) {
    postTagService.taging(postTag);
  }

  // 게시물 수정 시 태그 삭제
  @DeleteMapping("/delete-tag")
  public void deleteTag(@RequestBody PostTag postTag) {
    postTagService.removeTag(postTag);
  }
  
}
