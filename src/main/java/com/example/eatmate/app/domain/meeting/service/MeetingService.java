package com.example.eatmate.app.domain.meeting.service;

import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.meeting.domain.ParticipantLimit;
import com.example.eatmate.app.domain.meeting.domain.repository.DeliveryMeetingRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.OfflineMeetingRepository;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingResponseDto;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingService {
	private final DeliveryMeetingRepository deliveryMeetingRepository;
	private final MemberRepository memberRepository;
	private final OfflineMeetingRepository offlineMeetingRepository;

	public CreateDeliveryMeetingResponseDto createDeliveryMeeting(
		CreateDeliveryMeetingRequestDto createDeliveryMeetingRequestDto, Long memberId) {

		Member member = getMember(memberId);

		validateParticipantLimit(
			createDeliveryMeetingRequestDto.getMaxParticipants(),
			createDeliveryMeetingRequestDto.getIsLimited()
		);

		DeliveryMeeting deliveryMeeting = DeliveryMeeting.builder()
			.meetingName(createDeliveryMeetingRequestDto.getMeetingName())
			.meetingDescription(createDeliveryMeetingRequestDto.getMeetingDescription())
			.genderRestriction(createDeliveryMeetingRequestDto.getGenderRestriction())
			.participantLimit(ParticipantLimit.builder()
				.isLimited(createDeliveryMeetingRequestDto.getIsLimited())
				.maxParticipants(createDeliveryMeetingRequestDto.getMaxParticipants())
				.build())
			.foodCategory(createDeliveryMeetingRequestDto.getFoodCategory())
			.storeName(createDeliveryMeetingRequestDto.getStoreName())
			.pickupLocation(createDeliveryMeetingRequestDto.getPickupLocation())
			.orderDeadline(createDeliveryMeetingRequestDto.getOrderDeadline())
			.accountNumber(createDeliveryMeetingRequestDto.getAccountNumber())
			.accountHolder(createDeliveryMeetingRequestDto.getAccountHolder())
			.createdBy(member)
			.build();

		return CreateDeliveryMeetingResponseDto.of(deliveryMeetingRepository.save(deliveryMeeting));
	}

	public CreateOfflineMeetingResponseDto createOfflineMeeting(
		CreateOfflineMeetingRequestDto createOfflineMeetingRequestDto, Long memberId) {
		Member member = getMember(memberId);

		validateParticipantLimit(
			createOfflineMeetingRequestDto.getMaxParticipants(),
			createOfflineMeetingRequestDto.getIsLimited()
		);

		OfflineMeeting offlineMeeting = OfflineMeeting.builder()
			.meetingName(createOfflineMeetingRequestDto.getMeetingName())
			.meetingDescription(createOfflineMeetingRequestDto.getMeetingDescription())
			.genderRestriction(createOfflineMeetingRequestDto.getGenderRestriction())
			.participantLimit(ParticipantLimit.builder()
				.isLimited(createOfflineMeetingRequestDto.getIsLimited())
				.maxParticipants(createOfflineMeetingRequestDto.getMaxParticipants())
				.build())
			.meetingPlace(createOfflineMeetingRequestDto.getMeetingPlace())
			.meetingDate(createOfflineMeetingRequestDto.getMeetingDate())
			.createdBy(member)
			.build();

		return CreateOfflineMeetingResponseDto.of(offlineMeetingRepository.save(offlineMeeting));
	}

	// 참여 인원 제한 검증 로직
	private void validateParticipantLimit(Long participantLimit, Boolean isLimited) {
		// isLimited가 false(무제한)인데 participantLimit가 null이 아닌 경우
		if (!isLimited && participantLimit != null) {
			throw new CommonException(ErrorCode.INVALID_PARTICIPANT_LIMIT);
		}

		// isLimited가 true(제한있음)인 경우의 검증
		if (isLimited) {
			// participantLimit가 null이거나 2~10 범위를 벗어나는 경우
			if (participantLimit == null || participantLimit < 2 || participantLimit > 10) {
				throw new CommonException(ErrorCode.INVALID_PARTICIPANT_LIMIT);
			}
		}
	}

	// 회원 정보 조회 메소드
	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));
	}
}
