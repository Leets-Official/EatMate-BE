package com.example.eatmate.app.domain.member.domain;

import lombok.Getter;

@Getter
public enum Gender {
	MALE("남자"),
	FEMALE("여성");

	private final String genderType;

	Gender(String genderType) {
		this.genderType = genderType;
	}

}
