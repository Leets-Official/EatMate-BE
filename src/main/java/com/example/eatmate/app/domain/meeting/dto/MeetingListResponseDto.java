package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;

import lombok.Getter;

@Getter
public class MeetingListResponseDto {
	private String meetingType;
	private Long id;
	private String meetingName;
	private MeetingStatus meetingStatus;
	private Long participantCount;
	private String storeName;    // DeliveryMeeting인 경우
	private String meetingPlace; // OfflineMeeting인 경우
	private LocalDateTime orderDeadline;  // DeliveryMeeting인 경우
	private LocalDateTime meetingDate; // OfflineMeeting인 경우

	public MeetingListResponseDto(
		String type,
		Long id,
		String meetingName,
		MeetingStatus meetingStatus,
		String storeName,
		String meetingPlace,
		LocalDateTime orderDeadline,
		LocalDateTime meetingDate,
		Long participantCount
	) {
		this.meetingType = type;
		this.id = id;
		this.meetingName = meetingName;
		this.meetingStatus = meetingStatus;
		this.storeName = storeName;
		this.meetingPlace = meetingPlace;
		this.orderDeadline = orderDeadline;
		this.meetingDate = meetingDate;
		this.participantCount = participantCount;
	}

}

