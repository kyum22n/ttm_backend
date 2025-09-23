package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Pet;

@Mapper
public interface PetDao {
  public List<Pet> selectAllByPetId();
  public Pet selectByPetId(Integer petId);
  public int insert(Pet pet);
  public int update(Pet pet);
  public int delete(Integer petId);
}

