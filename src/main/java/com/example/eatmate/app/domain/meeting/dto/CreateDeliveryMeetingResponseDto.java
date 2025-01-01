package com.example.eatmate.app.domain.meeting.dto;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateDeliveryMeetingResponseDto {

	private Long meetingId;

	@Builder
	public CreateDeliveryMeetingResponseDto(Long meetingId) {
		this.meetingId = meetingId;
	}

	public static CreateDeliveryMeetingResponseDto from(DeliveryMeeting deliveryMeeting) {
		return CreateDeliveryMeetingResponseDto.builder()
			.meetingId(deliveryMeeting.getId())
			.build();
	}
}
