package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.PetDao;
import com.example.demo.dto.Pet;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetService {
	@Autowired
	private PetDao petDao;

	// // 반려견 등록
	// public void register(Pet pet) {
	// 	petDao.insertPet(pet);

	// 	if (pet.getPetAttachData() != null && pet.getPetAttachData().length > 0) {
	// 		petDao.insertPetImage(pet);
	// 	}
	// }
	
	// 반려견 1마리 정보 보기
	public Pet getPet(Integer petId) {
		Pet pet = petDao.selectByPetId(petId);
		return pet;
	}
	
	// 특정 반려인의 모든 반려견 정보 보기
	public List<Pet> getAllPetByUserId(Integer petUserId){
		List<Pet> pet = petDao.selectAllPetByUserId(petUserId);
		// log.info("pet: {}", pet);
		return pet;
	}
	
	// 반려견 수정하기
	public Pet update(Pet pet) {
		petDao.updatePet(pet);
		if (pet.getPetAttachData() != null && pet.getPetAttachData().length > 0) {
			petDao.updatePetImage(pet);
		}
		Pet dbPet = petDao.selectByPetId(pet.getPetId());
		return dbPet;
	}

	// 반려견 정보 삭제
	public int remove(Integer petId) {
		Pet pet = petDao.selectByPetId(petId);
		if(pet == null){
			return 0;
		}

		if (pet.getPetAttachData() != null && pet.getPetAttachData().length > 0) {
			petDao.deletePetImage(petId);
		}
		int rows = petDao.deletePet(petId);

		return rows;
	}
}
