package com.example.eatmate.app.domain.meeting.domain;

public enum GenderRestriction {
	MALE("남성"),
	FEMALE("여성"),
	ALL("무관"),
	;

	private final String description;

	GenderRestriction(String description) {
		this.description = description;
	}

}
