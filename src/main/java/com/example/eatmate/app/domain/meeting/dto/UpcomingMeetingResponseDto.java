package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;

import lombok.Getter;

@Getter
public class UpcomingMeetingResponseDto {
	private String nickname;
	private LocalDateTime meetingTime;
	private String meetingLocation;
	private OfflineMeetingCategory offlineMeetingCategory;
	private String type;

	public UpcomingMeetingResponseDto(String nickname, LocalDateTime meetingTime, String meetingLocation,
		OfflineMeetingCategory offlineMeetingCategory, String type) {
		this.nickname = nickname;
		this.meetingTime = meetingTime;
		this.meetingLocation = meetingLocation;
		this.offlineMeetingCategory = offlineMeetingCategory;
		this.type = type;

	}
}
