package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;

import lombok.Getter;

@Getter
public class UpcomingMeetingResponseDto {
	private Long id; //id, 모임 이름 추가
	private String nickname;
	private LocalDateTime meetingTime;
	private String meetingLocation;
	private OfflineMeetingCategory offlineMeetingCategory;
	private String type;
	private String meetingName;

	public UpcomingMeetingResponseDto(Long id, String nickname, LocalDateTime meetingTime, String meetingLocation,
		OfflineMeetingCategory offlineMeetingCategory, String type, String meetingName) {
		this.id = id;
		this.nickname = nickname;
		this.meetingTime = meetingTime;
		this.meetingLocation = meetingLocation;
		this.offlineMeetingCategory = offlineMeetingCategory;
		this.type = type;
		this.meetingName = meetingName;
	}
}
