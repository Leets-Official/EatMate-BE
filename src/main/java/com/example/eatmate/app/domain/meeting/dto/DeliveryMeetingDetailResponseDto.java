package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.member.domain.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DeliveryMeetingDetailResponseDto {
	private String meetingName;
	private String meetingDescription;
	private Long participantCount;
	private Long maxParticipants;
	private String genderRestriction;
	private String storeName;
	private String pickupLocation;
	private LocalDateTime orderDeadline;
	private String meetingLeaderName;
	private Long meetingLeaderHostedMeetingCount;

	@Builder
	private DeliveryMeetingDetailResponseDto(String meetingName, String meetingDescription, Long participantCount,
		Long maxParticipants, String genderRestriction, String storeName, String pickupLocation,
		LocalDateTime orderDeadline, Member meetingLeader, Long meetingLeaderHostedMeetingCount) {
		this.meetingName = meetingName;
		this.meetingDescription = meetingDescription;
		this.participantCount = participantCount;
		this.maxParticipants = maxParticipants;
		this.genderRestriction = genderRestriction;
		this.storeName = storeName;
		this.pickupLocation = pickupLocation;
		this.orderDeadline = orderDeadline;
		this.meetingLeaderName = meetingLeader.getNickname();
		this.meetingLeaderHostedMeetingCount = meetingLeaderHostedMeetingCount;
	}

	public static DeliveryMeetingDetailResponseDto of(DeliveryMeeting deliveryMeeting, Long participantCount,
		Member meetingLeader, Long meetingLeaderHostedMeetingCount) {
		return DeliveryMeetingDetailResponseDto.builder()
			.meetingName(deliveryMeeting.getMeetingName())
			.meetingDescription(deliveryMeeting.getMeetingDescription())
			.participantCount(participantCount)
			.maxParticipants(deliveryMeeting.getParticipantLimit().getMaxParticipants())
			.genderRestriction(deliveryMeeting.getGenderRestriction().getDescription())
			.storeName(deliveryMeeting.getStoreName())
			.pickupLocation(deliveryMeeting.getPickupLocation())
			.orderDeadline(deliveryMeeting.getOrderDeadline())
			.meetingLeader(meetingLeader)
			.meetingLeaderHostedMeetingCount(meetingLeaderHostedMeetingCount)
			.build();
	}
}
