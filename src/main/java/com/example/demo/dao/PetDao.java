package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

  // 특정 반려인의 반려견 수 
  public int countAllPetByUserId(Integer petUserId);

  // 메인 페이지에서 펫 프로필 랜덤으로 불러오기
	public List<Pet> selectRandomPets(int limit);

  // 유저 ID에 등록된 첫 펫 ID 가져오기
  public Pet selectFirstPetInfoByUserId(@Param("userId") int userId);
  // 펫 주인 userId 조회
  public Integer selectUserIdByPetId(Integer petId);
}

