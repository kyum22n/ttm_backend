package com.example.demo.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.Pet;

@Mapper
public interface PetDao {
  public int insert(Pet pet);
  public int update(Pet pet);
  public int delete(int petId);
  public List<Pet> selectByPetId();
}
