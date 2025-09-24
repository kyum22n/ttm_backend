package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.PetDao;
import com.example.demo.dto.Pet;

@Service
//미완!!!!!!!!!!!! hsh
public class PetRegisterService {
	@Autowired
	private PetDao petDao;

	public void register(Pet pet) {
		petDao.insert(pet);
	}

	public Pet info(Integer petId) {
		Pet pet = petDao.selectByPetId(petId);
		return pet;
	}
	public Pet update(Pet pet) {
		Pet dbPet = petDao.selectByPetId(pet.getPetId());


		petDao.update(dbPet);
		return dbPet;
	}

	public enum RemoveResult {
		SUCCESS, FAIL
	}

	public RemoveResult remove(Integer petId) {
		int rows = petDao.delete(petId);
		if (rows == 0) {
			return RemoveResult.FAIL;
		} else {
			return RemoveResult.SUCCESS;
		}
	}
}
