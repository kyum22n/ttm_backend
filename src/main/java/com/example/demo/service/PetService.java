package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.PetDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dto.Pet;
import com.example.demo.dto.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetService {
	@Autowired
	private PetDao petDao;

	@Autowired
	private UserDao userDao;

	// 반려견 등록
	public Pet register(Pet pet) throws Exception {
		petDao.insertPet(pet);
		MultipartFile mf = pet.getPetAttach();
		if (mf != null && !mf.isEmpty()) {
			pet.setPetAttachOname(mf.getOriginalFilename());
			pet.setPetAttachType(mf.getContentType());
			pet.setPetAttachData(mf.getBytes());
			petDao.insertPetImage(pet);
		}
		
		return petDao.selectByPetId(pet.getPetId());
	}

	// 반려견 1마리 정보 보기
	public Pet getPet(Integer petId) {
		Pet pet = petDao.selectByPetId(petId);
		return pet;
	}

	// 특정 반려인의 모든 반려견 정보 보기
	public List<Pet> getAllPetByUserId(Integer petUserId) {
		List<Pet> pet = petDao.selectAllPetByUserId(petUserId);
		User user = userDao.selectByUserId(petUserId);
		if (user == null) {
			throw new IllegalArgumentException("사용자 없음.");
		}
		if (pet == null || pet.isEmpty()) {
			throw new NoSuchElementException("등록된 반려견이 없습니다.");
		}
		return pet;
	}

	// 반려견 수정하기
	@Transactional
	public Pet update(Pet pet) throws Exception {
		// userId 검사 보류(예외 처리)
		MultipartFile mf = pet.getPetAttach();
		if (mf != null && !mf.isEmpty()) {
			pet.setPetAttachOname(mf.getOriginalFilename());
			pet.setPetAttachType(mf.getContentType());
			pet.setPetAttachData(mf.getBytes());
			petDao.updatePetImage(pet);
		}
		petDao.updatePet(pet);

		return petDao.selectByPetId(pet.getPetId());
	}

	// 반려견 정보 삭제
	public int remove(Integer petId) {
		Pet pet = petDao.selectByPetId(petId);
		if (pet == null) {
			return 0;
		}

		petDao.deletePetImage(petId);

		int rows = petDao.deletePet(petId);

		return rows;
	}
}
