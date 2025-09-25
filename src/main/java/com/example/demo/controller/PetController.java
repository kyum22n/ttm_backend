package com.example.demo.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    Pet dbPet = petService.info(pet.getPetId());

    return dbPet;
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
  
    Pet dbPet = petService.info(pet.getPetId());
  
    return dbPet;
    
  }

  // 반려견 정보 삭제하기
  @DeleteMapping("/remove")
  public String petRemove(@RequestParam("petId") Integer petId){
    petService.remove(petId);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("result", "success");

    return jsonObject.toString(); // {"result": "success"}
  }

}
