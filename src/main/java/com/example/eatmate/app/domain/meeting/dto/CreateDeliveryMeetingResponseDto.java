package com.example.eatmate.app.domain.meeting.dto;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;

import lombok.Getter;

@Getter
public class CreateDeliveryMeetingResponseDto {

	private Long meetingId;

	public CreateDeliveryMeetingResponseDto(DeliveryMeeting deliveryMeeting) {
		this.meetingId = deliveryMeeting.getId();
	}
}
