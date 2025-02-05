package com.example.eatmate.app.domain.meeting.dto;

import lombok.Getter;

@Getter
public class UpcomingMeetingResultDto {
	private UpcomingMeetingResponseDto upcomingMeetingResponseDto;
	private Boolean isOwn;

	public UpcomingMeetingResultDto (UpcomingMeetingResponseDto upcomingMeetingResponseDto, Boolean isOwn) {
		this.upcomingMeetingResponseDto = upcomingMeetingResponseDto;
		this.isOwn = isOwn;
	}
}
