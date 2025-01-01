package com.example.eatmate.app.domain.meeting.dto;

import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;

import lombok.Builder;

public class CreateOfflineMeetingResponseDto {
	private Long meetingId;

	@Builder
	private CreateOfflineMeetingResponseDto(OfflineMeeting offlineMeeting) {
		this.meetingId = offlineMeeting.getId();
	}

	public static CreateOfflineMeetingResponseDto of(OfflineMeeting offlineMeeting) {
		return CreateOfflineMeetingResponseDto.builder()
			.offlineMeeting(offlineMeeting)
			.build();
	}
}
