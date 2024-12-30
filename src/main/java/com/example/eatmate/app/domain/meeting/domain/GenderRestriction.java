package com.example.eatmate.app.domain.meeting.domain;

public enum GenderRestriction {
	ALL("모두 참여"),
	SAME_GENDER("같은 성별");

	private final String description;


	GenderRestriction(String description) {
		this.description = description;
	}

}
