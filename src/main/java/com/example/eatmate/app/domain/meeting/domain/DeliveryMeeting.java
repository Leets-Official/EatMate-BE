package com.example.eatmate.app.domain.meeting.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryMeeting extends Meeting {

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private FoodCategory foodCategory;

	@Column(nullable = false)
	private String storeName;

	@Column(nullable = false)
	private String pickupLocation; // 현재 지도 API 관련 정보가 없어 임시로 String 자료형 선언

	@Column(nullable = false)
	@Future
	private LocalDateTime orderDeadline;

	@Column(nullable = false)
	private String accountNumber;

	@Column(nullable = false)
	private String accountHolder;

	// public static DeliveryMeeting createDeliveryMeeting(String meetingName, String description,
	// 	GenderRestriction genderRestriction, boolean isUnlimited, Long maxParticipants, FoodCategory foodCategory,
	// 	String storeName, String pickupLocation, LocalDateTime orderDeadline, String accountNumber,
	// 	String accountHolder, Member member) {
	// 	return DeliveryMeeting.builder()
	// 		.meetingName(meetingName)
	// 		.description(description)
	// 		.genderRestriction(genderRestriction)
	// 		.participantLimit(ParticipantLimit.builder()
	// 			.isUnlimited(isUnlimited)
	// 			.maxParticipants(maxParticipants)
	// 			.build())
	// 		.foodCategory(foodCategory)
	// 		.storeName(storeName)
	// 		.pickupLocation(pickupLocation)
	// 		.orderDeadline(orderDeadline)
	// 		.accountNumber(accountNumber)
	// 		.accountHolder(accountHolder)
	// 		.createdBy(member)
	// 		.build();
	// }
}
