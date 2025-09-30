package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Walk;
import com.example.demo.service.WalkService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/walk")
public class WalkController {
    @Autowired
    private WalkService walkService;

    // 사용자 아이디로 1:1 산책 내역 불러오기
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> walkList(@RequestParam("userId") Integer userId) {
        Map<String, Object> map = new HashMap<>();
        List<Walk> walkList = walkService.getWalkListByUserId(userId);

        if(walkList.isEmpty()){
            map.put("message", "1:1 산책 내역이 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        } else {
            map.put("walkList", walkList);        
            return ResponseEntity.ok(map);
        }
    }
    
    // 사용자 아이디로 1:1 산책 신청받은 내역 불러오기
    @GetMapping("/apply/receive-list")
    public ResponseEntity<Map<String, Object>> walkApplyReceiveList(@RequestParam("receiveUserId") Integer receiveUserId) {
        Map<String, Object> map = new HashMap<>();
        List<Walk> walkReceiveList = walkService.getWalkApplyListByReceiveUserId(receiveUserId);
        
        if(walkReceiveList.isEmpty()) {
            map.put("message", "1:1 산책 신청받은 내역이 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        } else {
            map.put("walkReceiveList", walkReceiveList);
            return ResponseEntity.ok(map);
        }
    }
    
    // 사용자 아이디로 1:1 산책 신청한 내역 불러오기
    @GetMapping("/apply/request-list")
    public ResponseEntity<Map<String, Object>> walkApplyRequestList(@RequestParam("requestUserId") Integer requestUserId) {
        Map<String, Object> map = new HashMap<>();
        List<Walk> walkRequestList = walkService.getWalkApplyListByRequestUserId(requestUserId);
        
        if(walkRequestList.isEmpty()) {
            map.put("message", "1:1 산책 신청한 내역이 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);            
        } else {
            map.put("walkRequestList", walkRequestList);
            return ResponseEntity.ok(map);
        }
        
    }
    
    // 1:1 산책 신청
    @PostMapping("/apply")
    public ResponseEntity<Map<String, String>> applyWalk(@RequestBody Walk walk) {
        Map<String, String> map = new HashMap<>();
        
        // 산책 신청자와 수락자가 같을 경우 실패
        if( walk.getRequestUserId().equals(walk.getReceiveUserId()) ) {
            map.put("message", "산책 신청자가 수락자와 같을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        } else {
            walkService.createWalkApply(walk);
            return ResponseEntity.ok(map);
        }
    }

    // 1:1 산책 신청 상태 변경
    @PutMapping("/apply/status")
    public ResponseEntity<Map<String, Object>> walkApplyStatus(@RequestParam("requestOneId") Integer requestOneId,
                                @RequestParam("rstatus") String rstatus,
                                @RequestParam("receiveUserId") Integer receiveUserId) {
        Map<String, Object> map = new HashMap<>();
        
        walkService.modifyWalkApplyStatus(requestOneId, rstatus, receiveUserId);
        
        map.put("message", "1:1 산책 신청 상태가 변경되었습니다.");

        return ResponseEntity.ok(map);
    }

    // 1:1 산책 시작
    @PutMapping("/start")
    public ResponseEntity<Map<String, String>> walkStart(@RequestParam("requestOneId") Integer requestOneId,
                          @RequestParam("userId") Integer userId) {
        Map<String, String> map = new HashMap<>();
        
        walkService.modifyWalkStartedAt(requestOneId, userId);
        
        map.put("message", "1:1 산책이 시작되었습니다.");
        
        return ResponseEntity.ok(map);
    }
    
    // 1:1 산책 종료
    @PutMapping("/end")
    public ResponseEntity<Map<String, String>> walkEnd(@RequestParam("requestOneId") Integer requestOneId,
                                       @RequestParam("userId") Integer userId) {
        Map<String, String> map = new HashMap<>();
        
        walkService.modifyWalkEndedAt(requestOneId, userId);
        
        map.put("message", "1:1 산책이 종료되었습니다.");
        
        return ResponseEntity.ok(map);
    }
    
    // 1:1 산책 기록 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> walkDelete(@RequestParam("requestOneId") Integer requestOneId,
                                          @RequestParam("userId") Integer userId) {
        Map<String, String> map = new HashMap<>();
        
        walkService.removeWalk(requestOneId, userId);
        map.put("message", "1:1 산책 기록이 삭제되었습니다.");
        
        return ResponseEntity.ok(map);
    }

}
