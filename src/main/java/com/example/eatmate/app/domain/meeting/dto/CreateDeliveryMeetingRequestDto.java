package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;
import com.example.eatmate.app.domain.meeting.domain.ParticipantLimit;
import com.example.eatmate.app.domain.member.domain.Gender;
import com.example.eatmate.app.domain.member.domain.Mbti;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateDeliveryMeetingRequestDto {
	private String meetingName;
	private String description;
	private GenderRestriction genderRestriction;
	private boolean isUnlimited;
	private Long maxParticipants;
	private FoodCategory foodCategory;
	private String storeName;
	private String pickupLocation;
	private LocalDateTime orderDeadline;
	private String accountNumber;
	private String accountHolder;
}

