package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Walk;
import com.example.demo.service.WalkService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Map<String, Object> walkList(@RequestParam Integer userId) {
        List<Walk> walkList = walkService.getWalkListByUserId(userId);
        Map<String, Object> map = new HashMap<>();

        map.put("result", walkList.isEmpty() ? "fail" : "success");
        map.put("walkList", walkList);        

        return map;
    }

    // 사용자 아이디로 1:1 산책 신청받은 내역 불러오기
    @GetMapping("/apply/receive-list")
    public Map<String, Object> walkApplyReceiveList(@RequestParam Integer receiveUserId) {
        List<Walk> walkReceiveList = walkService.getWalkApplyListByReceiveUserId(receiveUserId);

        Map<String, Object> map = new HashMap<>();
        
        map.put("result", walkReceiveList.isEmpty() ? "fail" : "success");
        map.put("walkReceiveList", walkReceiveList);

        return map;
    }
    
    // 사용자 아이디로 1:1 산책 신청한 내역 불러오기
    @GetMapping("/apply/request-list")
    public Map<String, Object> walkApplyRequestList(@RequestParam Integer requestUserId) {
        List<Walk> walkRequestList = walkService.getWalkApplyListByRequestUserId(requestUserId);

        Map<String, Object> map = new HashMap<>();
        
        map.put("result", walkRequestList.isEmpty() ? "fail" : "success");
        map.put("walkRequestList", walkRequestList);

        return map;
    }
    
    // 1:1 산책 신청
    @PostMapping("/apply")
    public Map<String, String> applyWalk(@RequestBody Walk walk) {
        Map<String, String> map = new HashMap<>();
        
        // 산책 신청자와 수락자가 같을 경우 실패
        if( walk.getRequestUserId().equals(walk.getReceiveUserId()) ) {
            map.put("result", "fail");
        } else {
            walkService.createWalkApply(walk);
            map.put("result", "success");
        }

        return map;
    }

    // 1:1 산책 신청 상태 변경
    @PutMapping("/apply/status")
    public Map<String, Object> walkApplyStatus(@RequestParam Integer requestOneId,
                                @RequestParam String rstatus,
                                @RequestParam Integer receiveUserId) {
        Map<String, Object> map = new HashMap<>();
        
        int rows = walkService.modifyWalkApplyStatus(requestOneId, rstatus, receiveUserId);
        
        if( rows <= 0 ) {
            map.put("result", "fail");
        } else {
            map.put("result", "success");
        }

        return map;
    }

    // 1:1 산책 시작
    @PutMapping("/start")
    public Map<String, String> walkStart(@RequestParam Integer requestOneId,
                          @RequestParam Integer userId) {
        Map<String, String> map = new HashMap<>();
        
        int rows = walkService.modifyWalkStartedAt(requestOneId, userId);
        
        if( rows <= 0 ) {
            map.put("result", "fail");
        } else {
            map.put("result", "success");
        }
        
        return map;
        
    }
    
    // 1:1 산책 종료
    @PutMapping("/end")
    public Map<String, String> walkEnd(@RequestParam Integer requestOneId,
    @RequestParam Integer userId) {
        Map<String, String> map = new HashMap<>();
        
        int rows = walkService.modifyWalkEndedAt(requestOneId, userId);
        
        if( rows <= 0 ) {
            map.put("result", "fail");
        } else {
            map.put("result", "success");
        }
        
        return map;
        
    }
    
    // 1:1 산책 기록 삭제
    @DeleteMapping("/delete")
    public Map<String, String> walkDelete(@RequestParam Integer requestOneId,
                                          @RequestParam Integer userId) {
        Map<String, String> map = new HashMap<>();
        
        int rows = walkService.removeWalk(requestOneId, userId);
        
        if( rows <= 0 ) {
            map.put("result", "fail");
        } else {
            map.put("result", "success");
        }
        
        return map;
    }

}
