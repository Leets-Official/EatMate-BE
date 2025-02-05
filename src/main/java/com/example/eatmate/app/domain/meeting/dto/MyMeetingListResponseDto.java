package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;

import lombok.Getter;

@Getter
public class MyMeetingListResponseDto {
	private String meetingType;
	private Long id;
	private String meetingName;
	private MeetingStatus meetingStatus;
	private String meetingDescription;
	private Long maxParticipants;
	private OfflineMeetingCategory offlineMeetingCategory;
	private LocalDateTime createdAt;
	private String location;
	private LocalDateTime dueDateTime;
	private Long participantCount;
	private LocalDateTime lastChatAt;

	public MyMeetingListResponseDto(
		String meetingType,
		Long id,
		String meetingName,
		MeetingStatus meetingStatus,
		String meetingDescription,
		Long maxParticipants,
		OfflineMeetingCategory offlineMeetingCategory,
		LocalDateTime createdAt,
		String location,
		LocalDateTime dueDateTime,
		Long participantCount,
		LocalDateTime lastChatAt) {

		this.meetingType = meetingType;
		this.id = id;
		this.meetingName = meetingName;
		this.meetingStatus = meetingStatus;
		this.meetingDescription = meetingDescription;
		this.maxParticipants = maxParticipants;
		this.offlineMeetingCategory = offlineMeetingCategory;
		this.createdAt = createdAt;
		this.location = location;
		this.dueDateTime = dueDateTime;
		this.participantCount = participantCount;
		this.lastChatAt = lastChatAt;
	}
}
