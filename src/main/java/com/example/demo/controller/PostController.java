package com.example.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Comment;
import com.example.demo.dto.Pager;
import com.example.demo.dto.Participate;
import com.example.demo.dto.Post;
import com.example.demo.dto.PostTag;
import com.example.demo.dto.Tag;
import com.example.demo.service.CommentService;
import com.example.demo.service.ParticipateService;
import com.example.demo.service.PostService;
import com.example.demo.service.PostTagService;
import com.example.demo.service.TagService;

import lombok.extern.slf4j.Slf4j;

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

  @Autowired
  private ParticipateService participateService;

  // 게시물 작성
  @PostMapping(value = "/write", consumes = "multipart/form-data")
  public Post postWrite(@ModelAttribute Post post) throws IOException {
    return postService.write(post);
  }

  // 전체 게시물 목록 불러오기(페이징)
  @GetMapping("/list")
  public ResponseEntity<Map<String, Object>> postList(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {
    int totalRows = postService.getTotalRows();
    Pager pager = new Pager(10, 5, totalRows, pageNo);

    Map<String, Object> map = new HashMap<>();
    List<Post> list = postService.getPostListByPage(pager);

    if(list.isEmpty()) {
      map.put("message", "불러올 게시물이 없습니다.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    } else {
      map.put("pager", pager);
      map.put("posts", list);
      return ResponseEntity.ok(map);
    }

  }

  // 특정 사용자 게시물 목록 불러오기(페이징)
  @GetMapping("/{userId}/posts")
  public ResponseEntity<Map<String, Object>> userPostList(
      @PathVariable("userId") Integer userId,
      @RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {

    int totalRows = postService.getTotalRowsByUserId(userId);
    Pager pager = new Pager(10, 5, totalRows, pageNo);

    Map<String, Object> map = new HashMap<>();
    List<Post> list = postService.getPostListByUserId(userId);

    if(list == null) {
      map.put("message", "불러올 게시물이 없습니다.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    } else {
      map.put("pager", pager);
      map.put("posts", list);
      return ResponseEntity.ok(map);
    }
  }

  // 게시물 상세보기
  @GetMapping("/detail")
  public ResponseEntity<Map<String, Object>> postDetail(@RequestParam("postId") Integer postId) {

    Post post = postService.postDetail(postId);
    List<Comment> comments = commentService.getCommentListByPostId(postId);
    List<Tag> tags = postTagService.getTagNames(postId);

    Map<String, Object> map = new HashMap<>();
    map.put("post", post);
    map.put("comments", comments);
    map.put("tags", tags);

    return ResponseEntity.ok(map);
  }


  // 게시물 수정
  @PutMapping(value = "/update", consumes = "multipart/form-data")
  public ResponseEntity<Map<String, Object>> postUpdate(@ModelAttribute Post post,
                         @RequestParam(name = "imageMode", defaultValue = "append") String imageMode) throws IOException {
    Map<String, Object> map = new HashMap<>();
    postService.modifyPostWithImages(post, imageMode);
    Post updatePost =  postService.postDetail(post.getPostId());
    
    map.put("message", "게시물이 수정되었습니다.");
    map.put("post", updatePost);

    return ResponseEntity.ok(map);
  }

  // 게시물 삭제
  @DeleteMapping("/delete")
  public ResponseEntity<Map<String, Object>> postDelete(@RequestParam("postId") Integer postId) {
    Map<String, Object> map = new HashMap<>();
    
    postService.removePost(postId);
    map.put("message", "게시물이 삭제되었습니다.");
    
    return ResponseEntity.ok(map);
  }
  
  // 댓글 작성
  @PostMapping("/comment-write")
  public ResponseEntity<Map<String, Object>> commentWrite(Comment comment) {
    Map<String, Object> map = new HashMap<>();
    
    commentService.writeComment(comment);
    map.put("message", "댓글이 작성되었습니다.");
    
    return ResponseEntity.ok(map);
  }
  
  // 댓글 수정
  @PutMapping("/comment-update")
  public ResponseEntity<Map<String, Object>> commentUpdate(Comment comment) {
    Map<String, Object> map = new HashMap<>();
    
    commentService.modifyComment(comment);
    map.put("message", "댓글이 수정되었습니다.");
      
    return ResponseEntity.ok(map);
  }
  
  // 댓글 삭제
  @DeleteMapping("/comment-delete")
  public ResponseEntity<Map<String, Object>> commentDelete(@RequestParam("commentId") Integer commentId) {
    Map<String, Object> map = new HashMap<>();
    
    commentService.deleteComment(commentId);
    map.put("message", "댓글이 삭제되었습니다.");
    
    return ResponseEntity.ok(map);
  }
  
  // 전체 태그 목록 조회
  @GetMapping("/tags")
  public ResponseEntity<Map<String, Object>> tagList() {
    Map<String, Object> map = new HashMap<>();
    List<Tag> tags = tagService.getAllTags();
    
    map.put("tags", tags);

    return ResponseEntity.ok(map);
  }
  
  // 게시물에 태그 달기
  @PostMapping("/add-tag")
  public ResponseEntity<Map<String, Object>> addTag(@RequestBody PostTag postTag) {
    Map<String, Object> map = new HashMap<>();
    
    postTagService.taging(postTag);
    map.put("message", "태그가 등록되었습니다.");

    return ResponseEntity.ok(map);
  }
  
  // 게시물 수정 시 태그 삭제
  @DeleteMapping("/delete-tag")
  public ResponseEntity<Map<String, Object>> deleteTag(@RequestBody PostTag postTag) {
    Map<String, Object> map = new HashMap<>();

    postTagService.removeTag(postTag);
    map.put("message", "태그가 삭제되었습니다.");

    return ResponseEntity.ok(map);
  }

  // 그룹 산책 모집글만 조회
  @GetMapping("/groupwalk/recruitment-list")
  public Map<String, Object> groupWalkRecruitment() {
    Map<String, Object> map = new HashMap<>();
    List<Post> groupWalkPost = postService.getAllGroupWalkPost();

    if (groupWalkPost == null) {
      map.put("result", "fail");
      map.put("message", "산책 모집글이 없습니다.");
    } else {
      map.put("result", "success");
      map.put("groupWalkPost", groupWalkPost);
    }

    return map;
  }

  // 그룹 산책 완료된 글만 조회
  @GetMapping("/groupwalk/ended-list")
  public Map<String, Object> endedGroupWalk() {
    Map<String, Object> map = new HashMap<>();
    List<Post> endedGroupWalk = postService.getAllEndedGroupWalk();

    if (endedGroupWalk == null) {
      map.put("result", "fail");
      map.put("message", "완료된 그룹 산책이 없습니다.");
    } else {
      map.put("result", "success");
      map.put("message", endedGroupWalk);
    }

    return map;
  }

  // 이제 이 컨트롤러는 제껍니다
  // 산책 신청(자기 자신을 participates에 등록)
  @PostMapping("/groupwalk/apply")
  public void groupWalkApply(@RequestBody Participate participate) {
    participateService.groupWalkApply(participate);
  }

  static class ApproveReq {
    public int postId;
    public int userId;
  }

  @PutMapping(value = "/groupwalk/approve", consumes = "application/json")
  public void groupWalkApprove(@RequestBody ApproveReq req) {
    participateService.groupWalkApprove(req.postId, req.userId);
  }

  static class RejectReq {
    public int postId;
    public int userId;
  }

  @PutMapping(value = "/groupwalk/reject", consumes = "application/json")
  public void groupWalkReject(@RequestBody RejectReq req) {
    participateService.groupWalkReject(req.postId, req.userId);
  }

  static class CompleteReq {
    public int postId;
    public int userId;
  }

  @PutMapping(value = "/groupwalk/complete", consumes = "application/json")
  public void groupWalkComplete(@RequestBody CompleteReq req) {
    participateService.groupWalkComplete(req.postId, req.userId);
  }

  static class CancelReq {
    public int postId;
    public int userId;
  }

  @DeleteMapping(value = "/groupwalk/cancel", consumes = "application/json")
  public void groupWalkCancel(@RequestBody CancelReq req) {
    participateService.groupWalkApplyCancel(req.postId, req.userId);
  }

  // 특정 글의 대기(P) 참가자 목록
  @GetMapping("/groupwalk/{postId}/participants/pending")
  public List<Participate> listPendingParticipants(@PathVariable("postId") int postId) {
    return participateService.listPendingByPost(postId);
  }

  // 특정 글의 승인(A) 참가자 목록
  @GetMapping("/groupwalk/{postId}/participants/approved")
  public List<Participate> listApprovedParticipants(@PathVariable("postId") int postId) {
    return participateService.listApprovedByPost(postId);
  }

  //
  // @PutMapping("/groupwalk/apply-end/now")
  // public Post setApplyEndNow(@RequestParam("postId") int postId) {
  // return postService.markWApplyEndedNow(postId);
  // }

  // @PutMapping("/groupwalk/start/now")
  // public Post setWalkStartNow(@RequestParam("postId") int postId) {
  // return postService.markWalkStartedNow(postId);
  // }

  // @PutMapping("/groupwalk/end/now")
  // public Post setWalkEndNow(@RequestParam("postId") int postId) {
  // return postService.markWalkEndedNow(postId);
  // }

  @PutMapping("/groupwalk/now")
  public Post markNow(@RequestParam("postId") int postId, @RequestParam("code") int code) {
    return postService.markWalkByCode(postId, code);
  }
}
