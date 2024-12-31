package com.example.eatmate.app.domain.meeting.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.repository.DeliveryMeetingRepository;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingResponseDto;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingService {
	private final DeliveryMeetingRepository deliveryMeetingRepository;
	private final MemberRepository memberRepository;

	public CreateDeliveryMeetingResponseDto createDeliveryMeeting(
		CreateDeliveryMeetingRequestDto createDeliveryMeetingRequestDto, Long memberId) {

		Member member = memberRepository.findById(memberId).orElseThrow();

		DeliveryMeeting deliveryMeeting = DeliveryMeeting.createDeliveryMeeting(
			createDeliveryMeetingRequestDto.getMeetingName(),
			createDeliveryMeetingRequestDto.getDescription(), createDeliveryMeetingRequestDto.getGenderRestriction(),
			createDeliveryMeetingRequestDto.isUnlimited(),
			createDeliveryMeetingRequestDto.getMaxParticipants(), createDeliveryMeetingRequestDto.getFoodCategory(),
			createDeliveryMeetingRequestDto.getStoreName(), createDeliveryMeetingRequestDto.getPickupLocation(),
			createDeliveryMeetingRequestDto.getOrderDeadline(), createDeliveryMeetingRequestDto.getAccountNumber(),
			createDeliveryMeetingRequestDto.getAccountHolder(), member);

		return new CreateDeliveryMeetingResponseDto(deliveryMeetingRepository.save(deliveryMeeting));
	}
}
