package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

  // 반려견 정보 등록하기
  @PostMapping("/register")
  public Pet petRegister(@ModelAttribute Pet pet) throws Exception{
    MultipartFile mf = pet.getPetAttach();
    if(mf != null && mf.isEmpty()){
      pet.setPetAttachOname(mf.getOriginalFilename());
      pet.setPetAttachType(mf.getContentType());
      pet.setPetAttachData(mf.getBytes());
    }
    petService.register(pet);

    Pet dbPet = petService.getPet(pet.getPetId());

    return dbPet;
  }
  
  // 특정 반려견 정보 보기
  @GetMapping("/find")
  public Pet petFind(@RequestParam("petId") Integer petId) {
    Pet dbPet = petService.getPet(petId);
    return dbPet;
  }

  @GetMapping("/find-allpetbyuser")
  public Map<String, Object> findAllpetbyuser(@RequestParam("petUserId") Integer petUserId) {
    Map<String, Object> map = new HashMap<>();
    List<Pet> dbpets = petService.getAllPetByUserId(petUserId);
    if(petUserId == null && petUserId <= 0){
      map.put("result", "faill");
      map.put("message", "사용자 없음");
    } else{
      map.put("result", "success");
      map.put("pet", dbpets);
    }
    return map;
  }
  
  

  // 반려견 정보 수정하기
  @PutMapping("/update")
  public Pet petUpdate(@ModelAttribute Pet pet) throws Exception{
    MultipartFile mf = pet.getPetAttach();
    if(mf != null && mf.isEmpty()){
      pet.setPetAttachOname(mf.getOriginalFilename());
      pet.setPetAttachType(mf.getContentType());
      pet.setPetAttachData(mf.getBytes());
    }
    petService.update(pet);
  
    Pet dbPet = petService.getPet(pet.getPetId());
  
    return dbPet;
    
  }

  // 반려견 정보 삭제하기
  @DeleteMapping("/remove")
  public Map<String, String> petRemove(@RequestParam("petId") Integer petId){
    Map<String, String> map = new HashMap<>();
    int pet = petService.remove(petId);

    if(petId == null && pet < 0){
      map.put("result", "fail");
    }else{
      map.put("result", "success");
    }
    return map;
  }

}
