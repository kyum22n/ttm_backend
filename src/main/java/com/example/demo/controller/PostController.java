package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
  public Post postWrite(@ModelAttribute Post post) throws Exception {
    // 기존 단일 파일 처리 → 서비스에서 이미 처리하므로 여기선 로깅만
    if (post.getPostAttach() != null && !post.getPostAttach().isEmpty()) {
      log.info("단일 이미지: {}", post.getPostAttach().getOriginalFilename());
    }

    // 다중 파일 로깅 (실제 저장은 서비스에서 한다)
    if (post.getPostAttaches() != null) {
      for (var mf : post.getPostAttaches()) {
        if (mf != null && !mf.isEmpty()) {
          log.info("다중 이미지: {}", mf.getOriginalFilename());
        }
      }
    }

    postService.writePost(post);
    return postService.postDetail(post.getPostId());
  }

  // 전체 게시물 목록 불러오기(페이징)
  @GetMapping("/list")
  public Map<String, Object> postList(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {
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
      @RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {

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
  @PutMapping(value = "/update", consumes = "multipart/form-data")
  public Post postUpdate(
      @ModelAttribute Post post,
      @RequestParam(name = "imageMode", defaultValue = "replace") String imageMode,
      @RequestParam(name = "clearImages", defaultValue = "N") String clearImages) throws Exception {

    // 단일 파일 로깅 (서비스에서 저장 처리함)
    if (post.getPostAttach() != null && !post.getPostAttach().isEmpty()) {
      log.info("단일 이미지(수정): {}", post.getPostAttach().getOriginalFilename());
    }

    // 다중 파일 로깅
    if (post.getPostAttaches() != null) {
      log.info("다중 이미지 개수(수정): {}", post.getPostAttaches().size());
      for (var mf : post.getPostAttaches()) {
        if (mf != null && !mf.isEmpty()) {
          log.info("다중 이미지(수정): {} ({} bytes)", mf.getOriginalFilename(), mf.getSize());
        }
      }
    }

    postService.modifyPostWithImages(post, imageMode, "Y".equalsIgnoreCase(clearImages));
    return postService.postDetail(post.getPostId());
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
    if (postId == null && rows < 0) {
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
    if (commentId == null && rows < 0) {
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
  @PutMapping("/groupwalk/apply-end/now")
  public Post setApplyEndNow(@RequestParam("postId") int postId) {
    return postService.markWApplyEndedNow(postId);
  }

  @PutMapping("/groupwalk/start/now")
  public Post setWalkStartNow(@RequestParam("postId") int postId) {
    return postService.markWalkStartedNow(postId);
  }

  @PutMapping("/groupwalk/end/now")
  public Post setWalkEndNow(@RequestParam("postId") int postId) {
    return postService.markWalkEndedNow(postId);
  }

}
