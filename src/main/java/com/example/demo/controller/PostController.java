package com.example.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.example.demo.enums.ParticipateStatus;
import com.example.demo.interceptor.Login;
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
  // @Login
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

    if (list.isEmpty()) {
      map.put("message", "불러올 게시물이 없습니다.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    } else {
      map.put("pager", pager);
      map.put("posts", list);
      return ResponseEntity.ok(map);
    }

  }

  // 특정 사용자 게시물 목록 불러오기
  @GetMapping("/{userId}/posts")
  public ResponseEntity<Map<String, Object>> userPostList(@PathVariable("userId") Integer userId) {

    // int totalRows = postService.getTotalRowsByUserId(userId);  // 특정 사용자 게시물 수

    Map<String, Object> map = new HashMap<>();
    List<Post> list = postService.getPostListByUserId(userId);

    if (list == null) {
      map.put("message", "불러올 게시물이 없습니다.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    } else {
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
    Post updatePost = postService.postDetail(post.getPostId());

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
  public ResponseEntity<Map<String, Object>> commentWrite(@RequestBody Comment comment) {
    Map<String, Object> map = new HashMap<>();

    commentService.writeComment(comment);
    map.put("message", "댓글이 작성되었습니다.");

    return ResponseEntity.ok(map);
  }

  // 댓글 수정
  @PutMapping("/comment-update")
  public ResponseEntity<Map<String, Object>> commentUpdate(@RequestBody Comment comment) {
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
  public ResponseEntity<Map<String, String>> groupWalkRecruitment() {
    List<Post> groupWalkPost = postService.getAllGroupWalkPost(); // 실패는 전역 예외처리

    int count = 0;
    if (groupWalkPost != null) {
      count = groupWalkPost.size();
    }

    Map<String, String> map = new HashMap<>();
    map.put("result", "success");
    if (count == 0) {
      map.put("message", "그룹 산책 모집글이 없습니다.");
    } else {
      map.put("message", "그룹 산책 모집글 목록을 조회했습니다.");
    }
    map.put("count", String.valueOf(count));

    return ResponseEntity.ok(map);
  }

  // 그룹 산책 완료된 글만 조회
  @GetMapping("/groupwalk/ended-list")
  public ResponseEntity<Map<String, String>> endedGroupWalk() {
    List<Post> endedGroupWalk = postService.getAllEndedGroupWalk(); // 실패는 전역 예외처리

    int count = 0;
    if (endedGroupWalk != null) {
      count = endedGroupWalk.size();
    }

    Map<String, String> map = new HashMap<>();
    map.put("result", "success");
    if (count == 0) {
      map.put("message", "완료된 그룹 산책이 없습니다.");
    } else {
      map.put("message", "완료된 그룹 산책 목록을 조회했습니다.");
    }
    map.put("count", String.valueOf(count));

    return ResponseEntity.ok(map);
  }

  // 이제 이 컨트롤러는 제껍니다
  // 산책 신청(자기 자신을 participates에 등록)

  // 예: PUT /post/groupwalk/APPLY (APPROVE/REJECT/COMPLETE/CANCEL 도 동일)
  @PostMapping("/groupwalk/{status}")
  public ResponseEntity<Map<String, Object>> handle(@PathVariable("status") String status,
      @RequestBody Participate participate) {

    ParticipateStatus st = ParticipateStatus.parse(status);
    participateService.handleByStatus(participate, st);

    Map<String, Object> map = new HashMap<>();
    map.put("result", "success");
    map.put("status", st.name()); // P/A/R/C/CANCEL
    map.put("postId", participate.getPostId());
    map.put("userId", participate.getUserId());
    return ResponseEntity.ok(map);
  }

  // 특정 글의 대기(P) 참가자 목록
  @GetMapping("/groupwalk/{postId}/participants/pending")
  public ResponseEntity<Map<String, Object>> listPendingParticipants(@PathVariable("postId") int postId) {
    List<Participate> list = participateService.listPendingByPost(postId);

    if (list == null || list.isEmpty()) {
      Map<String, Object> map = new HashMap<>();
      map.put("message", "대기(P) 참가자가 없습니다.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    }

    Map<String, Object> map = new HashMap<>();
    map.put("result", "success");
    map.put("count", list.size());
    map.put("participants", list);
    return ResponseEntity.ok(map);
  }

  // 특정 글의 승인(A) 참가자 목록
  @GetMapping("/groupwalk/{postId}/participants/approved")
  public ResponseEntity<Map<String, Object>> listApprovedParticipants(@PathVariable("postId") int postId) {
    List<Participate> list = participateService.listApprovedByPost(postId);

    if (list == null || list.isEmpty()) {
      Map<String, Object> map = new HashMap<>();
      map.put("message", "승인(A) 참가자가 없습니다.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    }

    Map<String, Object> map = new HashMap<>();
    map.put("result", "success");
    map.put("count", list.size());
    map.put("participants", list);
    return ResponseEntity.ok(map);
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

  // @PutMapping("/groupwalk/now")
  // public Post markNow(@RequestParam("postId") int postId, @RequestParam("code")
  // int code) {
  // return postService.markWalkByCode(postId, code);
  // }

  @PutMapping("/groupwalk/now")
  public ResponseEntity<Map<String, String>> markNow(@RequestParam("postId") int postId,
      @RequestParam("code") int code) {

    // 실패 케이스는 서비스에서 예외를 던지고 전역 예외처리가 응답
    Post postUpdated = postService.markWalkByCode(postId, code);

    Map<String, String> map = new HashMap<>();
    map.put("result", "success");

    String message;
    switch (code) {
    case 1:
      message = "신청 마감 시간이 기록되었습니다.";
      break;
    case 2:
      message = "산책 시작 시간이 기록되었습니다.";
      break;
    case 3:
      message = "산책 종료 시간이 기록되었습니다.";
      break;
    default:
      message = "산책 상태가 업데이트되었습니다.";
    }
    map.put("message", message);
    map.put("postId", String.valueOf(postUpdated.getPostId()));
    map.put("code", String.valueOf(code));

    return ResponseEntity.ok(map);
  }
}
