package com.example.eatmate.app.domain.meeting.service;

import static com.example.eatmate.app.domain.meeting.domain.GenderRestriction.*;
import static com.example.eatmate.app.domain.meeting.domain.MeetingStatus.*;
import static com.example.eatmate.app.domain.meeting.domain.ParticipantRole.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;
import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.app.domain.meeting.domain.MeetingParticipant;
import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;
import com.example.eatmate.app.domain.meeting.domain.ParticipantLimit;
import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.meeting.domain.repository.DeliveryMeetingRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingParticipantRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingSortType;
import com.example.eatmate.app.domain.meeting.domain.repository.OfflineMeetingRepository;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.DeliveryMeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MyMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.OfflineMeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpcomingMeetingResponseDto;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.global.common.util.SecurityUtils;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;
import com.example.eatmate.global.response.CursorResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingService {

	private final DeliveryMeetingRepository deliveryMeetingRepository;
	private final OfflineMeetingRepository offlineMeetingRepository;
	private final MeetingParticipantRepository meetingParticipantRepository;
	private final MeetingRepository meetingRepository;
	private final SecurityUtils securityUtils;

	// 참여자와 모임 성별제한 일치 여부 확인 메소드
	private void validateGenderRestriction(CreateOfflineMeetingRequestDto requestDto, Member member) {
		validateGenderRestrictionCommon(requestDto.getGenderRestriction(), member);
	}

	private void validateGenderRestriction(CreateDeliveryMeetingRequestDto requestDto, Member member) {
		validateGenderRestrictionCommon(requestDto.getGenderRestriction(), member);
	}

	private void validateGenderRestrictionCommon(GenderRestriction genderRestriction, Member member) {
		if (genderRestriction != ALL && !genderRestriction.toString().equals(member.getGender().toString())) {
			throw new CommonException(ErrorCode.INVALID_GENDER_RESTRICTION);
		}
	}

	// 배달 모임 생성
	@Transactional
	public CreateDeliveryMeetingResponseDto createDeliveryMeeting(
		CreateDeliveryMeetingRequestDto createDeliveryMeetingRequestDto, UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);

		validateGenderRestriction(createDeliveryMeetingRequestDto, member);

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
			.meetingStatus(ACTIVE)
			.foodCategory(createDeliveryMeetingRequestDto.getFoodCategory())
			.storeName(createDeliveryMeetingRequestDto.getStoreName())
			.pickupLocation(createDeliveryMeetingRequestDto.getPickupLocation())
			.orderDeadline(LocalDateTime.now().plusMinutes(createDeliveryMeetingRequestDto.getOrderDeadline()))
			.accountNumber(createDeliveryMeetingRequestDto.getAccountNumber())
			.accountHolder(createDeliveryMeetingRequestDto.getAccountHolder())
			.build();

		deliveryMeeting = deliveryMeetingRepository.save(deliveryMeeting);

		meetingParticipantRepository.save(
			MeetingParticipant.createMeetingParticipant(member, deliveryMeeting, HOST)); // 참여 등록
		return CreateDeliveryMeetingResponseDto.from(deliveryMeeting);
	}

	// 밥, 술 모임 생성
	@Transactional
	public CreateOfflineMeetingResponseDto createOfflineMeeting(
		CreateOfflineMeetingRequestDto createOfflineMeetingRequestDto, UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);

		validateGenderRestriction(createOfflineMeetingRequestDto, member);

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
			.meetingStatus(ACTIVE)
			.meetingPlace(createOfflineMeetingRequestDto.getMeetingPlace())
			.meetingDate(createOfflineMeetingRequestDto.getMeetingDate())
			.offlineMeetingCategory(createOfflineMeetingRequestDto.getOfflineMeetingCategory())
			.build();

		offlineMeeting = offlineMeetingRepository.save(offlineMeeting);
		meetingParticipantRepository.save(
			MeetingParticipant.createMeetingParticipant(member, offlineMeeting, HOST)); // 참여 등록

		return CreateOfflineMeetingResponseDto.from(offlineMeeting);
	}

	// 모임 참여 메소드 (공통)
	private void joinMeeting(Long meetingId, UserDetails userDetails, boolean isDeliveryMeeting) {
		Member member = securityUtils.getMember(userDetails);
		Meeting meeting;

		if (isDeliveryMeeting) {
			meeting = deliveryMeetingRepository.findById(meetingId)
				.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));
		} else {
			meeting = offlineMeetingRepository.findById(meetingId)
				.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));
		}

		if (meeting.getMeetingStatus() == MeetingStatus.INACTIVE) {
			throw new CommonException(ErrorCode.MEETING_NOT_FOUND);
		}

		validateParticipantLimit(meeting);
		validateGenderRestriction(meeting, member);
		validateDuplicateParticipant(meeting, member);

		meetingParticipantRepository.save(MeetingParticipant.createMeetingParticipant(member, meeting, PARTICIPANT));
	}

	// 배달 모임 참여
	@Transactional
	public void joinDeliveryMeeting(Long meetingId, UserDetails userDetails) {
		joinMeeting(meetingId, userDetails, true);
	}

	// 밥, 술 모임 참여
	@Transactional
	public void joinOfflineMeeting(Long meetingId, UserDetails userDetails) {
		joinMeeting(meetingId, userDetails, false);
	}

	// 밥, 술 모임 목록 조회 메소드
	@Transactional(readOnly = true)
	public CursorResponseDto getOfflineMeetingList(OfflineMeetingCategory category,
		GenderRestriction genderRestriction, Long maxParticipant, Long minParticipant, MeetingSortType sortType,
		int pageSize, Long lasMeetingId, LocalDateTime lastDateTime) {
		List<MeetingListResponseDto> meetings = meetingRepository.findOfflineMeetingList(category, genderRestriction,
			maxParticipant, minParticipant, sortType, pageSize, lasMeetingId, lastDateTime);

		if (sortType == MeetingSortType.CREATED_AT) {
			return CursorResponseDto.ofIdAndCreatedAt(meetings, pageSize, MeetingListResponseDto::getMeetingId,
				MeetingListResponseDto::getCreatedAt);
		}

		if (sortType == MeetingSortType.MEETING_TIME) {
			return CursorResponseDto.ofIdAndMeetingTime(meetings, pageSize, MeetingListResponseDto::getMeetingId,
				MeetingListResponseDto::getDueDateTime);
		}

		if (sortType == MeetingSortType.PARTICIPANT_COUNT) {
			return CursorResponseDto.ofId(meetings, pageSize, MeetingListResponseDto::getMeetingId);
		}

		throw new CommonException(ErrorCode.INVALID_SORT_TYPE);
	}

	// 배달 모임 목록 조회 메소드
	@Transactional(readOnly = true)
	public CursorResponseDto getDeliveryMeetingList(FoodCategory category, GenderRestriction genderRestriction,
		Long maxParticipant, Long minParticipant, MeetingSortType sortType, int pageSize, Long lastMeetingId,
		LocalDateTime lastDateTime) {
		List<MeetingListResponseDto> meetings = meetingRepository.findDeliveryMeetingList(category, genderRestriction,
			maxParticipant,
			minParticipant, sortType, pageSize, lastMeetingId, lastDateTime);
		if (sortType == MeetingSortType.CREATED_AT) {
			return CursorResponseDto.ofIdAndCreatedAt(meetings, pageSize, MeetingListResponseDto::getMeetingId,
				MeetingListResponseDto::getCreatedAt);
		}

		if (sortType == MeetingSortType.MEETING_TIME) {
			return CursorResponseDto.ofIdAndMeetingTime(meetings, pageSize, MeetingListResponseDto::getMeetingId,
				MeetingListResponseDto::getDueDateTime);
		}

		if (sortType == MeetingSortType.PARTICIPANT_COUNT) {
			return CursorResponseDto.ofId(meetings, pageSize, MeetingListResponseDto::getMeetingId);
		}

		throw new CommonException(ErrorCode.INVALID_SORT_TYPE);

	}

	// 오프라인 모임 상세 조회 메소드
	@Transactional(readOnly = true)
	public OfflineMeetingDetailResponseDto getOfflineMeetingDetail(Long meetingId) {

		OfflineMeeting offlinemeeting = offlineMeetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		if (offlinemeeting.getMeetingStatus() == MeetingStatus.INACTIVE) {
			throw new CommonException(ErrorCode.MEETING_NOT_FOUND);
		}

		MeetingParticipant meetingParticipant = meetingParticipantRepository.findByMeetingAndRole(offlinemeeting, HOST)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND)); // 모임 주인 컬럼 확인

		Member member = meetingParticipant.getMember(); // 모임 주인 확인

		Long hostedMeetings = meetingParticipantRepository.countByMemberAndRole(meetingParticipant.getMember(),
			HOST); // 주인이 개최한 모임 수 확인

		Long participantCount = meetingParticipantRepository.countByMeeting_Id(meetingId); // 참여 인원 수

		return OfflineMeetingDetailResponseDto.of(offlinemeeting, participantCount, member, hostedMeetings);
	}

	// 배달 모임 상세 조회 메소드
	@Transactional(readOnly = true)
	public DeliveryMeetingDetailResponseDto getDeliveryMeetingDetail(Long meetingId) {
		DeliveryMeeting deliveryMeeting = deliveryMeetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		if (deliveryMeeting.getMeetingStatus() == MeetingStatus.INACTIVE) {
			throw new CommonException(ErrorCode.MEETING_NOT_FOUND);
		}

		MeetingParticipant meetingParticipant = meetingParticipantRepository.findByMeetingAndRole(deliveryMeeting, HOST)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND)); // 모임 주인 컬럼 확인

		Member member = meetingParticipant.getMember(); // 모임 주인 확인

		Long hostedMeetings = meetingParticipantRepository.countByMemberAndRole(member, HOST); // 주인이 개최한 모임 수 확인

		Long participantCount = meetingParticipantRepository.countByMeeting_Id(meetingId); // 참여 인원 수

		return DeliveryMeetingDetailResponseDto.of(deliveryMeeting, participantCount, member, hostedMeetings);
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

	// 참여 인원 제한 검증
	private void validateParticipantLimit(Meeting meeting) {
		Long meetingCount = meetingParticipantRepository.countByMeeting_Id(meeting.getId());
		Long participantLimit = meeting.getParticipantLimit().getMaxParticipants();
		Boolean isLimited = meeting.getParticipantLimit().isLimited();

		if (isLimited && meetingCount >= participantLimit) {
			throw new CommonException(ErrorCode.PARTICIPANT_LIMIT_EXCEEDED);
		}
	}

	// 성별 제한 검증
	private void validateGenderRestriction(Meeting meeting, Member member) {
		if (meeting.getGenderRestriction() != ALL
			&& !meeting.getGenderRestriction().toString().equals(member.getGender().toString())) {
			throw new CommonException(ErrorCode.GENDER_RESTRICTED_MEETING);
		}
	}

	// 중복 참가 검증
	private void validateDuplicateParticipant(Meeting meeting, Member member) {
		if (meetingParticipantRepository.existsByMeetingAndMember(meeting, member)) {
			throw new CommonException(ErrorCode.PARTICIPANT_ALREADY_EXISTS);
		}
	}

	// 내가 생성, 참여한 모임 조회
	@Transactional(readOnly = true)
	public CursorResponseDto<MyMeetingListResponseDto> getMyMeetingList(
		UserDetails userDetails,
		ParticipantRole role,
		Long lastMeetingId,
		LocalDateTime lastDateTime,
		int pageSize
	) {
		Member member = securityUtils.getMember(userDetails);
		List<MyMeetingListResponseDto> meetings = meetingRepository.findMyMeetingList(
			member.getMemberId(),
			role,
			null,
			lastMeetingId,
			lastDateTime,
			pageSize
		);
		return CursorResponseDto.ofIdAndMeetingTime(meetings, pageSize, MyMeetingListResponseDto::getId,
			MyMeetingListResponseDto::getDueDateTime);
	}

	// 내가 참여 중인 활성화된 모임 조회
	@Transactional(readOnly = true)
	public CursorResponseDto<MyMeetingListResponseDto> getMyActiveMeetingList(
		UserDetails userDetails,
		Long lastMeetingId,
		LocalDateTime lastDateTime,
		int pageSize
	) {
		Member member = securityUtils.getMember(userDetails);
		List<MyMeetingListResponseDto> meetings = meetingRepository.findMyMeetingList(
			member.getMemberId(),
			null,
			ACTIVE,
			lastMeetingId,
			lastDateTime,
			pageSize
		);
		return CursorResponseDto.ofIdAndMeetingTime(meetings, pageSize, MyMeetingListResponseDto::getId,
			MyMeetingListResponseDto::getDueDateTime);
	}

	// 가장 최근 미팅 조회
	@Transactional(readOnly = true)
	public UpcomingMeetingResponseDto getUpcomingMeeting(UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);

		// 임박한 미팅 조회
		UpcomingMeetingResponseDto upcomingMeeting = meetingRepository.findUpcomingMeeting(member.getMemberId());

		// 진행중인 미팅이 없는 경우
		if (upcomingMeeting == null) {
			throw new CommonException(ErrorCode.MEETING_NOT_FOUND);
		}

		return upcomingMeeting;
	}
