package com.example.eatmate.app.domain.meeting.dto;

import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;

import lombok.Builder;

public class CreateOfflineMeetingResponseDto {
	private Long meetingId;

	@Builder
	private CreateOfflineMeetingResponseDto(Long meetingId) {
		this.meetingId = meetingId;
	}

	public static CreateOfflineMeetingResponseDto from(OfflineMeeting offlineMeeting) {
		return CreateOfflineMeetingResponseDto.builder()
			.meetingId(offlineMeeting.getId())
			.build();
	}
}
