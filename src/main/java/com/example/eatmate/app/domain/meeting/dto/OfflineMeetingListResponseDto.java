package com.example.eatmate.app.domain.meeting.dto;

import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;

import lombok.Builder;

public class OfflineMeetingListResponseDto {
	private String meetingName;
	private String meetingDescription;
	private String meetingPlace;
	private Long participantCount;
	private Long maxParticipants;

	@Builder
	private OfflineMeetingListResponseDto(String meetingName, String meetingDescription, String meetingPlace, Long participantCount, Long maxParticipants) {
		this.meetingName = meetingName;
		this.meetingDescription = meetingDescription;
		this.meetingPlace = meetingPlace;
		this.participantCount = participantCount;
		this.maxParticipants = maxParticipants;
	}

	public static OfflineMeetingListResponseDto of(OfflineMeeting offlineMeeting, Long participantCount) {
		return OfflineMeetingListResponseDto.builder()
			.meetingName(offlineMeeting.getMeetingName())
			.meetingDescription(offlineMeeting.getMeetingDescription())
			.meetingPlace(offlineMeeting.getMeetingPlace())
			.participantCount(participantCount)
			.maxParticipants(offlineMeeting.getParticipantLimit().getMaxParticipants())
			.build();
	}
}
