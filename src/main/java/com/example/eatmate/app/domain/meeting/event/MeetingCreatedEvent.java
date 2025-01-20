package com.example.eatmate.app.domain.meeting.event;

import com.example.eatmate.app.domain.member.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingCreatedEvent {
	private final Long meetingId;
	private final Member host;
}

