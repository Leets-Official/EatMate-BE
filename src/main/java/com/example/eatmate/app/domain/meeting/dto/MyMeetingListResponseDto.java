package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;

import lombok.Getter;

@Getter
public class MyMeetingListResponseDto {
	private String meetingType;
	private Long id;
	private String meetingName;
	private MeetingStatus meetingStatus;
	private Long participantCount;
	private String location;
	private LocalDateTime dueDateTime;

	public MyMeetingListResponseDto(
		String type,
		Long id,
		String meetingName,
		MeetingStatus meetingStatus,
		String location,
		LocalDateTime dueDateTime,
		Long participantCount
	) {
		this.meetingType = type;
		this.id = id;
		this.meetingName = meetingName;
		this.meetingStatus = meetingStatus;
		this.location = location;
		this.dueDateTime = dueDateTime;
		this.participantCount = participantCount;
	}

}

