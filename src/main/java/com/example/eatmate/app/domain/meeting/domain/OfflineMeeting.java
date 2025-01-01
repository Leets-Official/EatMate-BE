package com.example.eatmate.app.domain.meeting.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
@NoArgsConstructor
public class OfflineMeeting extends Meeting {
	@Column(nullable = false)
	private String meetingPlace; // 현재 지도 API 관련 정보가 없어 임시로 String 자료형 선언

	@Column(nullable = false)
	@Future
	private LocalDateTime meetingDate;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OfflineMeetingCategory offlineMeetingCategory;

	// public static OfflineMeeting createOfflineMeeting(String meetingName, String description,
	// 	GenderRestriction genderRestriction,
	// 	boolean isUnlimited, Long maxParticipants, String meetingPlace, LocalDateTime meetingDate, Member member) {
	// 	return OfflineMeeting.builder()
	// 		.meetingName(meetingName)
	// 		.description(description)
	// 		.genderRestriction(genderRestriction)
	// 		.participantLimit(ParticipantLimit.builder()
	// 			.isUnlimited(isUnlimited)
	// 			.maxParticipants(maxParticipants)
	// 			.build())
	// 		.meetingPlace(meetingPlace)
	// 		.meetingDate(meetingDate)
	// 		.createdBy(member)
	// 		.build();
	// }
}
