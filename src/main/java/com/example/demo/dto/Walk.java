package com.example.demo.dto;

import java.util.Date;

public class Walk {
	private enum requestStatus{
		P, A, R, C
	};
	private Date walkStartedAt;
	private Date walkEndedAt;
}
