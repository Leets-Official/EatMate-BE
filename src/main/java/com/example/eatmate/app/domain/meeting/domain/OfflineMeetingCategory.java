package com.example.eatmate.app.domain.meeting.domain;

public enum OfflineMeetingCategory {
	BEVERAGE("술약"),
	MEAL("밥약");

	private final String description;

	OfflineMeetingCategory(String description) {
		this.description = description;
	}

}
