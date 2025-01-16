package com.example.eatmate.global.response;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class CursorInfo {
	private Long id;
	private LocalDateTime lastCreatedAt;
	private LocalDateTime lastMeetingTime;
	private Long lastParticipantCount;

	// 기본 생성자 (id와 시간만)
	public CursorInfo(Long id, LocalDateTime dateTime) {
		this.id = id;
		this.lastMeetingTime = dateTime;
	}

	// 전체 필드 생성자
	public CursorInfo(Long id, LocalDateTime createdAt, LocalDateTime meetingTime, Long participantCount) {
		this.id = id;
		this.lastCreatedAt = createdAt;
		this.lastMeetingTime = meetingTime;
		this.lastParticipantCount = participantCount;
	}
}
