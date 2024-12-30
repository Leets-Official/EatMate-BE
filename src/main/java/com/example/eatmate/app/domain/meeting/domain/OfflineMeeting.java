package com.example.eatmate.app.domain.meeting.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Future;

@Entity
public class OfflineMeeting extends Meeting {
	@Column
	private String meetingPlace; // 현재 지도 API 관련 정보가 없어 임시로 String 자료형 선언

	@Column
	@Future
	private LocalDateTime meetingDate;

}
