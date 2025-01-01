package com.example.eatmate.app.domain.meeting.dto;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateDeliveryMeetingResponseDto {

	private Long meetingId;

	@Builder
	public CreateDeliveryMeetingResponseDto(DeliveryMeeting deliveryMeeting) {
		this.meetingId = deliveryMeeting.getId();
	}

	public static CreateDeliveryMeetingResponseDto of(DeliveryMeeting deliveryMeeting) {
		return CreateDeliveryMeetingResponseDto.builder()
			.deliveryMeeting(deliveryMeeting)
			.build();
	}
}
