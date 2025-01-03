package com.example.eatmate.app.domain.meeting.domain;

import lombok.Getter;

@Getter
public enum GenderRestriction {
	MALE("남자들끼리만 모여요"),
	FEMALE("여자들끼리만 모여요"),
	ALL("아무나 모여요"),
	;

	private final String description;

	GenderRestriction(String description) {
		this.description = description;
	}

}
