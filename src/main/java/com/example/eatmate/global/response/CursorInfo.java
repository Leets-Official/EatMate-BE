package com.example.eatmate.global.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CursorInfo {
	private Long meetingId;
	private LocalDateTime lastCreatedAt;
	private LocalDateTime lastMeetingTime;

	// 전체 필드 생성자
	private CursorInfo(Long meetingId, LocalDateTime lastCreatedAt, LocalDateTime lastMeetingTime) {
		this.meetingId = meetingId;
		this.lastCreatedAt = lastCreatedAt;
		this.lastMeetingTime = lastMeetingTime;
	}

	public static CursorInfo withCreatedAt(Long meetingId, LocalDateTime createdAt) {
		return new CursorInfo(meetingId, createdAt, null);
	}

	public static CursorInfo withMeetingTime(Long meetingId, LocalDateTime meetingTime) {
		return new CursorInfo(meetingId, null, meetingTime);
	}

	public static CursorInfo onlyId(Long meetingId) {
		return new CursorInfo(meetingId, null, null);
	}
}
