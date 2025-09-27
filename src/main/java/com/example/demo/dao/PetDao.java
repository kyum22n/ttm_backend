package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Pet;

@Mapper
public interface PetDao {
  // 반려견
  public List<Pet> selectAllPetByUserId(Integer petUserId);
  public Pet selectByPetId(Integer petId);
  public int insertPet(Pet pet);
  public int updatePet(Pet pet);
  public int deletePet(Integer petId);

  // 반려견 좋아요
  public int increasePetLikecount(Integer petId);
  public int decreasePetLikecount(Integer petId);

  // 반려견 이미지
  public int insertPetImage(Pet pet);
  public int updatePetImage(Pet pet);
  public int deletePetImage(Integer petId);
  public Pet selectPetImageByPetId(Integer petId);

  // 특정 반려인의 반려견 수 
  public int countAllPetByUserId(Integer petUserId);
}

