package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Pet;
import com.example.demo.service.PetService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/pet")
public class PetController {
  @Autowired
  private PetService petService;

  // @PostMapping("/register")
  // public Map<String, Object> petRegister(@ModelAttribute Pet pet) {
  //   Map<String, Object> map = new HashMap<>();
  //   try {
  //     Pet result = petService.register(pet);
  //     map.put("result", "success");
  //     map.put("pet", result);
  //   } catch (Exception e) {
  //     map.put("result", "fail");
  //     map.put("message", e.getMessage());
  //   }
  //   return map;
  // }

  // 특정 반려견 정보 조회
  @GetMapping("/find")
  public Map<String, Object> petFind(@RequestParam("petId") Integer petId) {
    Map<String, Object> map = new HashMap<>();
    Pet dbPet = petService.getPet(petId);
    if (dbPet == null) {
      map.put("result", "fail");
      map.put("message", "반려견이 없습니다.");
    } else {
      map.put("result", "success");
      map.put("pet", dbPet);
    }
    return map;
  }

  // 사용자의 반려견 정보 모두 조회
  @GetMapping("/find-allpetbyuser")
  public Map<String, Object> findAllpetbyuser(@RequestParam("petUserId") Integer petUserId) {
    Map<String, Object> map = new HashMap<>();
    try {
      List<Pet> dbpets = petService.getAllPetByUserId(petUserId);
      map.put("result", "success");
      map.put("pet", dbpets);
    } catch (IllegalArgumentException e) {
      map.put("result", "fail");
      map.put("message", e.getMessage());
    } catch (NoSuchElementException e) {
      map.put("result", "fail");
      map.put("null", e.getMessage());
    }

    return map;
  }

  // 반려견 정보 수정하기
  @PutMapping("/update")
  public Map<String, Object> petUpdate(@ModelAttribute Pet pet) {
    Map<String, Object> map = new HashMap<>();

    try{
    Pet dbPet = petService.update(pet);
    map.put("result", "success");
    map.put("pet", dbPet);
    }catch(Exception e){
      map.put("result", "수정 실패");
    }
    return map;
  }

  // 반려견 정보 삭제하기
  @DeleteMapping("/remove")
  public Map<String, String> petRemove(@RequestParam("petId") Integer petId) {
    Map<String, String> map = new HashMap<>();
    int pet = petService.remove(petId);

    if (pet == 0) {
      map.put("result", "fail");
    } else {
      map.put("result", "success");
    }
    return map;
  }

}
