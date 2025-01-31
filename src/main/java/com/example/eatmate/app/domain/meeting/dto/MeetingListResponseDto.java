package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class MeetingListResponseDto {
	private Long meetingId;
	private String meetingName;
	private String meetingDescription;
	private Long currentParticipantCount;
	private Long maxParticipants;
	private String location;
	private LocalDateTime createdAt;
	private LocalDateTime dueDateTime;
	private LocalDateTime lastChatAt;

	public MeetingListResponseDto(Long meetingId, String meetingName, String meetingDescription,
		Long currentParticipantCount, Long maxParticipants, String location, LocalDateTime createdAt,
		LocalDateTime dueDateTime, LocalDateTime lastChatAt) {
		this.meetingId = meetingId;
		this.meetingName = meetingName;
		this.meetingDescription = meetingDescription;
		this.currentParticipantCount = currentParticipantCount;
		this.maxParticipants = maxParticipants;
		this.location = location;
		this.createdAt = createdAt;
		this.dueDateTime = dueDateTime;
		this.lastChatAt = lastChatAt;
	}
}

