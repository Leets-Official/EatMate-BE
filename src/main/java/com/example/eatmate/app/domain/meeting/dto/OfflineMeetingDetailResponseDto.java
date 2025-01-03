package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.member.domain.Member;

import lombok.Builder;

public class OfflineMeetingDetailResponseDto {
	private String meetingName;
	private String meetingDescription;
	private Long participantCount;
	private Long maxParticipants;
	private String genderRestriction;
	private String meetingPlace;
	private LocalDateTime meetingDate;
	private String meetingLeaderName;
	private Long meetingLeaderHostedMeetingCount;

	@Builder
	private OfflineMeetingDetailResponseDto(String meetingName, String meetingDescription, Long participantCount,
		Long maxParticipants, String genderRestriction, String meetingPlace,
		LocalDateTime meetingDate, Member meetingLeader, Long meetingLeaderHostedMeetingCount) {
		this.meetingName = meetingName;
		this.meetingDescription = meetingDescription;
		this.participantCount = participantCount;
		this.maxParticipants = maxParticipants;
		this.genderRestriction = genderRestriction;
		this.meetingPlace = meetingPlace;
		this.meetingDate = meetingDate;
		this.meetingLeaderName = meetingLeader.getName();
		this.meetingLeaderHostedMeetingCount = meetingLeaderHostedMeetingCount;

	}

	public static OfflineMeetingDetailResponseDto of(OfflineMeeting offlineMeeting, Long participantCount,
		Member meetingLeader, Long meetingLeaderHostedMeetingCount) {
		return OfflineMeetingDetailResponseDto.builder()
			.meetingName(offlineMeeting.getMeetingName())
			.meetingDescription(offlineMeeting.getMeetingDescription())
			.participantCount(participantCount)
			.maxParticipants(offlineMeeting.getParticipantLimit().getMaxParticipants())
			.genderRestriction(offlineMeeting.getGenderRestriction().getDescription())
			.meetingPlace(offlineMeeting.getMeetingPlace())
			.meetingDate(offlineMeeting.getMeetingDate())
			.meetingLeader(meetingLeader)
			.meetingLeaderHostedMeetingCount(meetingLeaderHostedMeetingCount)
			.build();
	}
}
