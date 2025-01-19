package com.example.eatmate.global.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CursorInfo {
	private Long id;
	private LocalDateTime lastCreatedAt;
	private LocalDateTime lastMeetingTime;

	// 전체 필드 생성자
	private CursorInfo(Long id, LocalDateTime lastCreatedAt, LocalDateTime lastMeetingTime) {
		this.id = id;
		this.lastCreatedAt = lastCreatedAt;
		this.lastMeetingTime = lastMeetingTime;
	}

	// id만 필요한 경우
	public CursorInfo(Long id) {
		this.id = id;
	}

	public static CursorInfo withCreatedAt(Long id, LocalDateTime createdAt) {
		return new CursorInfo(id, createdAt, null);
	}

	public static CursorInfo withMeetingTime(Long id, LocalDateTime meetingTime) {
		return new CursorInfo(id, null, meetingTime);
	}

	public static CursorInfo onlyId(Long id) {
		return new CursorInfo(id, null, null);
	}
}
