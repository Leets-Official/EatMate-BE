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
@DiscriminatorValue("DELIVERY")
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

	public void updateDeliveryMeeting(
		String meetingName,
		String description,
		FoodCategory foodCategory,
		String storeName,
		String pickupLocation,
		String accountNumber,
		String accountHolder,
		Image backgroundImage
	) {
		super.updateMeeting(meetingName, description, backgroundImage);

		this.foodCategory = foodCategory;
		this.storeName = storeName;
		this.pickupLocation = pickupLocation;
		this.accountNumber = accountNumber;
		this.accountHolder = accountHolder;
	}
}
