package com.example.eatmate.app.domain.meeting.domain;

public enum ParticipantRole {
	PARTICIPANT("참가자"),
	HOST("주최자");

	private final String description;

	ParticipantRole(String description) {
		this.description = description;
	}

}
