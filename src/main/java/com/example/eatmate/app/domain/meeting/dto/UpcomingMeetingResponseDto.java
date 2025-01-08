package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class UpcomingMeetingResponseDto {
	private String nickname;
	private LocalDateTime meetingTime;
	private String meetingLocation;

	public UpcomingMeetingResponseDto(String nickname, LocalDateTime meetingTime, String meetingLocation) {
		this.nickname = nickname;
		this.meetingTime = meetingTime;
		this.meetingLocation = meetingLocation;
	}
}
