package com.example.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class User {
	private Integer userId;
	private String loginId;
	private String password;
	private String uname;
	private String email;
	private String address;
	private Date birthDate;
	private Date createdAt;
	
}
