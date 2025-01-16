package com.example.eatmate.app.domain.meeting.domain;

import lombok.Getter;

@Getter
public enum MeetingStatus {
	ACTIVE("활성화"),
	INACTIVE("비활성화"),
	;
	private final String text;

	MeetingStatus(String text) {
		this.text = text;
	}

}
