package com.example.eatmate.app.domain.meeting.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;

@Entity
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
}
