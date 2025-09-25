package com.example.demo.service;

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

	// 반려견 등록
	public void register(Pet pet) {
		petDao.insertPet(pet);

		if (pet.getPetAttachData() != null && pet.getPetAttachData().length > 0) {
			petDao.insertPetImage(pet);
		}
	}

	// 반려견 1마리 정보 보기
	public Pet info(Integer petId) {
		Pet pet = petDao.selectByPetId(petId);
		return pet;
	}

	// 반려견 수정하기
	public Pet update(Pet pet) {
		Pet dbPet = petDao.selectByPetId(pet.getPetId());
		petDao.updatePet(dbPet);
		return dbPet;
	}

	// 반려견 정보 삭제
	public int remove(Integer petId) {
		Pet pet = petDao.selectByPetId(petId);

		if (pet.getPetAttachData() != null && pet.getPetAttachData().length > 0) {
			petDao.deletePetImage(petId);
		}
		int rows = petDao.deletePet(petId);

		return rows;
	}
}
