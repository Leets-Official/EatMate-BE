package com.example.eatmate.app.domain.meeting.domain;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.image.domain.Image;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
@DiscriminatorValue("OFFLINE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OfflineMeeting extends Meeting {
	@Column(nullable = false)
	private String meetingPlace; // 현재 지도 API 관련 정보가 없어 임시로 String 자료형 선언

	@Column(nullable = false)
	@Future
	private LocalDateTime meetingDate;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OfflineMeetingCategory offlineMeetingCategory;

	public void updateOfflineMeeting(
		String meetingName,
		String description,
		String meetingPlace,
		LocalDateTime meetingDate,
		OfflineMeetingCategory offlineMeetingCategory,
		Image backgroundImage) {

		super.updateMeeting(meetingName, description, backgroundImage);

		this.meetingPlace = meetingPlace;
		this.meetingDate = meetingDate;
		this.offlineMeetingCategory = offlineMeetingCategory;
	}
}
