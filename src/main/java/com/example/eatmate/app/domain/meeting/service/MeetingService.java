package com.example.eatmate.app.domain.meeting.service;

import static com.example.eatmate.app.domain.meeting.domain.GenderRestriction.*;
import static com.example.eatmate.app.domain.meeting.domain.ParticipantRole.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.app.domain.meeting.domain.MeetingParticipant;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;
import com.example.eatmate.app.domain.meeting.domain.ParticipantLimit;
import com.example.eatmate.app.domain.meeting.domain.repository.DeliveryMeetingRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingParticipantRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.OfflineMeetingRepository;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.DeliveryMeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.DeliveryMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.OfflineMeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.OfflineMeetingListResponseDto;
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
	private final MeetingParticipantRepository meetingParticipantRepository;

	// 배달 모임 생성
	@Transactional
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
			.orderDeadline(LocalDateTime.now().plusMinutes(createDeliveryMeetingRequestDto.getOrderDeadline()))
			.accountNumber(createDeliveryMeetingRequestDto.getAccountNumber())
			.accountHolder(createDeliveryMeetingRequestDto.getAccountHolder())
			.build();

		deliveryMeeting = deliveryMeetingRepository.save(deliveryMeeting);

		MeetingParticipant.createMeetingParticipant(member, deliveryMeeting, PARTICIPANT); // 참여 등록
		return CreateDeliveryMeetingResponseDto.from(deliveryMeeting);
	}

	// 밥, 술 모임 생성
	@Transactional
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
			.offlineMeetingCategory(createOfflineMeetingRequestDto.getOfflineMeetingCategory())
			.build();

		offlineMeeting = offlineMeetingRepository.save(offlineMeeting);
		MeetingParticipant.createMeetingParticipant(member, offlineMeeting, HOST); // 참여 등록

		return CreateOfflineMeetingResponseDto.from(offlineMeeting);
	}

	// 모임 참가 메소드
	@Transactional
	public void joinMeeting(Long meetingId, Long memberId) {
		Member member = getMember(memberId);
		Meeting meeting = deliveryMeetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		Long meetingCount = meetingParticipantRepository.countByMeeting_Id(meetingId); // 현재 참여 인원 수
		Long participantLimit = meeting.getParticipantLimit().getMaxParticipants(); // 참여 인원 제한 수
		Boolean isLimited = meeting.getParticipantLimit().isLimited(); // 인원 제한여부

		if (!isLimited && meetingCount >= participantLimit) { // 인원 제한이 있으면서 참여 인원이 제한을 초과한 경우
			throw new CommonException(ErrorCode.PARTICIPANT_LIMIT_EXCEEDED);
		}

		if (meeting.getGenderRestriction() != ALL && meeting.getGenderRestriction().equals(member.getGender())) {
			throw new CommonException(ErrorCode.GENDER_RESTRICTED_MEETING);
		}

		if (meetingParticipantRepository.existsByMeetingAndMember(meeting, member)) {
			throw new CommonException(ErrorCode.PARTICIPANT_ALREADY_EXISTS);
		}

		MeetingParticipant.createMeetingParticipant(member, meeting, PARTICIPANT);

	}

	// 밥, 술 모임 목록 조회 메소드
	@Transactional(readOnly = true)
	public List<OfflineMeetingListResponseDto> getOfflineMeetingList(OfflineMeetingCategory offlineMeetingCategory) {
		List<OfflineMeeting> meetings = offlineMeetingRepository.findAllByOfflineMeetingCategory(
			offlineMeetingCategory);

		return meetings.stream()
			.map(meeting -> {
				Long participantCount = meetingParticipantRepository.countByMeeting_Id(meeting.getId());
				return OfflineMeetingListResponseDto.of(meeting, participantCount);
			})
			.collect(Collectors.toList());
	}

	// 배달 모임 목록 조회 메소드
	@Transactional(readOnly = true)
	public List<DeliveryMeetingListResponseDto> getDeliveryMeetingList(FoodCategory foodCategory) {
		List<DeliveryMeeting> meetings = deliveryMeetingRepository.findAllByFoodCategory(foodCategory);

		return meetings.stream()
			.map(meeting -> {
				Long participantCount = meetingParticipantRepository.countByMeeting_Id(meeting.getId());
				return DeliveryMeetingListResponseDto.of(meeting, participantCount);
			})
			.collect(Collectors.toList());
	}

	// 오프라인 모임 상세 조회 메소드
	@Transactional(readOnly = true)
	public OfflineMeetingDetailResponseDto getOfflineMeetingDetail(Long meetingId) {

		OfflineMeeting offlinemeeting = offlineMeetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		Member member = meetingParticipantRepository.findByMeetingAndRole(offlinemeeting, HOST)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND)); // 모임 주인 확인

		Long HostedMeetings = meetingParticipantRepository.countByMemberAndAndRole(member, HOST); // 주인이 개최한 모임 수 확인

		Long participantCount = meetingParticipantRepository.countByMeeting_Id(meetingId); // 참여 인원 수

		return OfflineMeetingDetailResponseDto.of(offlinemeeting, participantCount, member, HostedMeetings);
	}

	@Transactional(readOnly = true)
	public DeliveryMeetingDetailResponseDto getDeliveryMeetingDetail(Long meetingId) {
		DeliveryMeeting deliveryMeeting = deliveryMeetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		Member member = meetingParticipantRepository.findByMeetingAndRole(deliveryMeeting, HOST)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND)); // 모임 주인 확인

		Long HostedMeetings = meetingParticipantRepository.countByMemberAndAndRole(member, HOST); // 주인이 개최한 모임 수 확인

		Long participantCount = meetingParticipantRepository.countByMeeting_Id(meetingId); // 참여 인원 수

		return DeliveryMeetingDetailResponseDto.of(deliveryMeeting, participantCount, member, HostedMeetings);
	}

	// 참여 인원 제한 검증 로직
	private void validateParticipantLimit(Long participantLimit, Boolean isLimited) {
		// isLimited가 false(무제한)인데 participantLimit가 null이 아닌 경우
		if (!isLimited && participantLimit != null) {
			throw new CommonException(ErrorCode.INVALID_PARTICIPANT_LIMIT);
		}

		// isLimited가 true(제한있음)인 경우의 검증
		if (isLimited) {
			// participantLimit가 null일 경우
			if (participantLimit == null) {
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



