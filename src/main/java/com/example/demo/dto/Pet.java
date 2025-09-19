package com.example.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class Pet {
	private Integer petId;
	private Integer weight;
	private String breed;
	private Date petBirthDate;
	private String petName;
	private String petDesc;
	private Integer petLikeCount;
	private enum petGender {
		M, F
	};
}
