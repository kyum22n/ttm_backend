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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.Pet;
import com.example.demo.service.PetService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/pet")
public class PetController {
  @Autowired
  private PetService petService;

  // 반려견 추가 등록
  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> petRegister(@ModelAttribute Pet pet) throws IOException {
    Map<String, Object> map = new HashMap<>();

    MultipartFile mf = pet.getPetAttach();
    if (mf == null || mf.isEmpty()) {
      map.put("result", "fail");
      map.put("message", "반려견 이미지는 필수 업로드입니다.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }
    Pet result = petService.register(pet);
    map.put("result", "success");
    map.put("pet", result);
    return ResponseEntity.ok(map);
  }

  // 특정 반려견 정보 조회
  @GetMapping("/find")
  public ResponseEntity<Map<String, Object>> petFind(@RequestParam("petId") Integer petId) {
    Map<String, Object> map = new HashMap<>();
    Pet dbPet = petService.getPet(petId);
    map.put("result", "success");
    map.put("pet", dbPet);
    return ResponseEntity.ok(map);
  }

  // 사용자의 반려견 정보 모두 조회
  @GetMapping("/find-allpetbyuser")
  public List<Pet> findAllpetbyuser(@RequestParam("petUserId") Integer petUserId) {
    return petService.getAllPetByUserId(petUserId);
  }

  // 반려견 정보 수정하기
  @PutMapping("/update")
  public Pet petUpdate(@ModelAttribute Pet pet) throws IOException {
    return petService.update(pet);
  }

  // 반려견 정보 삭제하기
  @DeleteMapping("/remove")
  public ResponseEntity<Map<String, Object>> petRemove(@RequestParam("petId") Integer petId) {
    Map<String, Object> map = new HashMap<>();
    int rows = petService.remove(petId);
    map.put("result", "success");
    map.put("rows", rows);
    return ResponseEntity.ok(map);
  }

  // 펫 이미지 조회 API
  @GetMapping("/image/{petId}")
  public ResponseEntity<byte[]> getMethodName(@PathVariable("petId") Integer petId) {
    Pet image = petService.getPetImage(petId);

    return ResponseEntity.ok()
            .header("Content-Type", image.getPetAttachType())
            .body(image.getPetAttachData());
  }
  

}
