package com.example.eatmate.app.domain.meeting.event;

import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingJoinedEvent {
	private final Long meetingId;
	private final UserDetails participant;
}
