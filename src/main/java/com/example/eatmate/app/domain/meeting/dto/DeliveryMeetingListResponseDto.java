package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;

import lombok.Builder;

public class DeliveryMeetingListResponseDto {
	private String meetingName;
	private String meetingDescription;
	private String storeName;
	private LocalDateTime orderDeadline;
	private Long participantCount;
	private Long maxParticipants;

	@Builder
	private DeliveryMeetingListResponseDto(String meetingName, String meetingDescription, String storeName, LocalDateTime orderDeadLine, Long participantCount, Long maxParticipants) {
		this.meetingName = meetingName;
		this.meetingDescription = meetingDescription;
		this.storeName = storeName;
		this.orderDeadline = orderDeadLine;
		this.participantCount = participantCount;
		this.maxParticipants = maxParticipants;
	}

	public static DeliveryMeetingListResponseDto of(DeliveryMeeting deliveryMeeting, Long participantCount) {
		return DeliveryMeetingListResponseDto.builder()
			.meetingName(deliveryMeeting.getMeetingName())
			.meetingDescription(deliveryMeeting.getMeetingDescription())
			.storeName(deliveryMeeting.getStoreName())
			.orderDeadLine(deliveryMeeting.getOrderDeadline())
			.participantCount(participantCount)
			.maxParticipants(deliveryMeeting.getParticipantLimit().getMaxParticipants())
			.build();
	}
}
