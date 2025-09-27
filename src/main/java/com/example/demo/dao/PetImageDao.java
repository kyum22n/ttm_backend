package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Pet;

@Mapper
public interface PetImageDao {
  // 반려견 이미지
  public int insertPetImage(Pet pet);
  public int updatePetImage(Pet pet);
  public int deletePetImage(Integer petId);
  public Pet selectPetImageByPetId(Integer petId);

}
