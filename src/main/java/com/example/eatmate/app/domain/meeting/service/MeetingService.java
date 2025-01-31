package com.example.eatmate.app.domain.meeting.service;

import static com.example.eatmate.app.domain.image.domain.ImageType.*;
import static com.example.eatmate.app.domain.meeting.domain.GenderRestriction.*;
import static com.example.eatmate.app.domain.meeting.domain.MeetingStatus.*;
import static com.example.eatmate.app.domain.meeting.domain.ParticipantRole.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.block.domain.Block;
import com.example.eatmate.app.domain.block.domain.repository.BlockRepository;
import com.example.eatmate.app.domain.image.domain.Image;
import com.example.eatmate.app.domain.image.service.ImageSaveService;
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
import com.example.eatmate.app.domain.meeting.dto.MeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MyMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpcomingMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpdateDeliveryMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.UpdateOfflineMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.event.HostMeetingDeleteEvent;
import com.example.eatmate.app.domain.meeting.event.MeetingCreatedEvent;
import com.example.eatmate.app.domain.meeting.event.MeetingJoinedEvent;
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
	private final BlockRepository blockRepository;
	private final ImageSaveService imageSaveService;
	private final SecurityUtils securityUtils;
	private final ApplicationEventPublisher eventPublisher;

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
	//채팅방 생성시 호스트는 동시에 채팅방에 참여되어야 함
	@Transactional
	public CreateDeliveryMeetingResponseDto createDeliveryMeeting(
		CreateDeliveryMeetingRequestDto createDeliveryMeetingRequestDto, UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);

		validateGenderRestriction(createDeliveryMeetingRequestDto, member);

		validateParticipantLimitConfiguration(
			createDeliveryMeetingRequestDto.getMaxParticipants(),
			createDeliveryMeetingRequestDto.getIsLimited()
		);

		Image backgroundImage = Optional.ofNullable(createDeliveryMeetingRequestDto.getBackgroundImage())
			.map(image -> imageSaveService.uploadImage(image, MEETING_BACKGROUND))
			.orElse(null);

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
			.bankName(createDeliveryMeetingRequestDto.getBankName())
			.backgroundImage(backgroundImage)
			.build();

		deliveryMeeting = deliveryMeetingRepository.save(deliveryMeeting);

		eventPublisher.publishEvent(new MeetingCreatedEvent(deliveryMeeting.getId(), member)); // 채팅방 생성

		meetingParticipantRepository.save(
			MeetingParticipant.createMeetingParticipant(member, deliveryMeeting, HOST)); // 참여 등록

		return CreateDeliveryMeetingResponseDto.from(deliveryMeeting);
	}

	// 밥, 술 모임 생성
	//모임생성시 호스트는 동시에 채팅방에 참여되어야 함
	@Transactional
	public CreateOfflineMeetingResponseDto createOfflineMeeting(
		CreateOfflineMeetingRequestDto createOfflineMeetingRequestDto, UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);

		validateGenderRestriction(createOfflineMeetingRequestDto, member);

		validateParticipantLimitConfiguration(
			createOfflineMeetingRequestDto.getMaxParticipants(),
			createOfflineMeetingRequestDto.getIsLimited()
		);

		Image backgroundImage = Optional.ofNullable(createOfflineMeetingRequestDto.getBackgroundImage())
			.map(image -> imageSaveService.uploadImage(image, MEETING_BACKGROUND))
			.orElse(null);

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
			.backgroundImage(backgroundImage)
			.build();

		offlineMeeting = offlineMeetingRepository.save(offlineMeeting);

		eventPublisher.publishEvent(new MeetingCreatedEvent(offlineMeeting.getId(), member)); // 채팅방 생성

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

		List<MeetingParticipant> meetingParticipant = meetingParticipantRepository.findByMeeting(meeting);
		List<Block> blocks = blockRepository.findAllByMember(member);

		boolean isBlocked = meetingParticipant.stream()
			.map(MeetingParticipant::getMember)  // 참여자들의 Member 추출
			.anyMatch(participantMember ->        // 참여자 중에 차단된 사용자가 있는지 확인
				blocks.stream()
					.anyMatch(block ->
						block.getBlockedMember().equals(participantMember)
					)
			);

		if (isBlocked) {
			throw new CommonException(ErrorCode.BLOCKED_MEMBER_CANNOT_JOIN);
		}

		if (meeting.getMeetingStatus() == MeetingStatus.INACTIVE) {
			throw new CommonException(ErrorCode.MEETING_NOT_FOUND);
		}

		validateMeetingParticipantCapacity(meeting);
		validateGenderRestriction(meeting, member);
		validateDuplicateParticipant(meeting, member);

		meetingParticipantRepository.save(MeetingParticipant.createMeetingParticipant(member, meeting, PARTICIPANT));
		eventPublisher.publishEvent(new MeetingJoinedEvent(meeting.getId(), userDetails)); // 채팅방 참여
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

		return getCursorResponseDto(sortType, pageSize, meetings);
	}

	// 배달 모임 목록 조회 메소드
	@Transactional(readOnly = true)
	public CursorResponseDto getDeliveryMeetingList(FoodCategory category, GenderRestriction genderRestriction,
		Long maxParticipant, Long minParticipant, MeetingSortType sortType, int pageSize, Long lastMeetingId,
		LocalDateTime lastDateTime) {
		List<MeetingListResponseDto> meetings = meetingRepository.findDeliveryMeetingList(category, genderRestriction,
			maxParticipant,
			minParticipant, sortType, pageSize, lastMeetingId, lastDateTime);

		return getCursorResponseDto(sortType, pageSize, meetings);

	}

	private CursorResponseDto getCursorResponseDto(MeetingSortType sortType, int pageSize,
		List<MeetingListResponseDto> meetings) {
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

	// 모임 상세조회(공통)
	public MeetingDetailResponseDto getMeetingDetail(Long meetingId, UserDetails userDetails) {
		Meeting meeting = meetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		Member member = securityUtils.getMember(userDetails);

		List<MeetingDetailResponseDto.ParticipantDto> participants = getParticipants(meeting, member.getMemberId());

		return MeetingDetailResponseDto.builder()
			.meetingType(meeting.getType())
			.meetingName(meeting.getMeetingName())
			.meetingDescription(meeting.getMeetingDescription())
			.genderRestriction(meeting.getGenderRestriction())
			.location(getLocation(meeting))
			.dueDateTime(getDueDateTime(meeting))
			.backgroundImage(Optional.ofNullable(meeting.getBackgroundImage()).map(Image::getImageUrl).orElse(null))
			.isOwner(isOwner(meeting, member.getMemberId()))
			.isCurrentUser(isCurrentUser(meeting, member))
			.participants(participants)
			.build();
	}

	private String getLocation(Meeting meeting) {
		if (meeting instanceof DeliveryMeeting) {
			return ((DeliveryMeeting)meeting).getPickupLocation();
		} else if (meeting instanceof OfflineMeeting) {
			return ((OfflineMeeting)meeting).getMeetingPlace();
		}
		throw new CommonException(ErrorCode.MEETING_NOT_FOUND);
	}

	private LocalDateTime getDueDateTime(Meeting meeting) {
		if (meeting instanceof DeliveryMeeting) {
			return ((DeliveryMeeting)meeting).getOrderDeadline();
		} else if (meeting instanceof OfflineMeeting) {
			return ((OfflineMeeting)meeting).getMeetingDate();
		}
		throw new CommonException(ErrorCode.MEETING_NOT_FOUND);
	}

	private Boolean isOwner(Meeting meeting, Long currentUserId) {
		return meetingParticipantRepository.findByMeetingAndRole(meeting, ParticipantRole.HOST)
			.map(participant -> participant.getMember().getMemberId().equals(currentUserId))
			.orElse(false);
	}

	private Boolean isCurrentUser(Meeting meeting, Member member) {
		return meetingParticipantRepository.existsByMeetingAndMember(meeting, member);
	}

	private List<MeetingDetailResponseDto.ParticipantDto> getParticipants(Meeting meeting, Long currentUserId) {
		return meetingParticipantRepository.findByMeeting(meeting).stream()
			.sorted((p1, p2) -> {
				// HOST가 있으면 먼저 정렬
				if (p1.getRole() == ParticipantRole.HOST)
					return -1;
				if (p2.getRole() == ParticipantRole.HOST)
					return 1;
				// 그 외에는 참가 시간 순으로 정렬
				return p1.getCreatedAt().compareTo(p2.getCreatedAt());
			})
			.map(participant -> MeetingDetailResponseDto.ParticipantDto.createParticipantDto(
				participant.getMember().getMemberId(),
				participant.getMember().getNickname(),
				Optional.ofNullable(participant.getMember().getProfileImage())
					.map(Image::getImageUrl)
					.orElse(null),
				participant.getRole() == ParticipantRole.HOST,
				participant.getMember().getMemberId().equals(currentUserId)
			))
			.collect(Collectors.toList());
	}

	// 모임 생성시 참여 인원 제한 검증 로직
	private void validateParticipantLimitConfiguration(Long participantLimit, Boolean isLimited) {
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

	// 참여시 인원 제한 검증
	private void validateMeetingParticipantCapacity(Meeting meeting) {
		List<MeetingParticipant> participants = meetingParticipantRepository.findParticipantsByMeetingIdWithLock(
			meeting.getId());
		Long meetingCount = Long.valueOf(participants.size());
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

	// 모임 주인의 모임 삭제(나가기) / 컨트롤러에서 직접 사용할 메소드
	@Transactional
	public void hostMeetingDelete(Long meetingId, UserDetails userDetails, boolean isPublishedByChatRoom) {
		Member member = securityUtils.getMember(userDetails);
		Meeting meeting = meetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		MeetingParticipant meetingParticipant = meetingParticipantRepository.findByMeetingAndMember(meeting, member)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		if (meetingParticipant.getRole() != HOST) {
			throw new CommonException(ErrorCode.NOT_MEETING_HOST);
		}

		if (meeting.getMeetingStatus() == MeetingStatus.INACTIVE) {
			throw new CommonException(ErrorCode.ALREADY_DELETED_MEETING);
		}

		// 참가자 수 확인 (HOST 제외한 참가자가 있는지)
		long participantCount = meetingParticipantRepository.countByMeetingAndRoleNot(meeting, HOST);
		if (participantCount > 0) {
			throw new CommonException(ErrorCode.CANNOT_DELETE_MEETING_WITH_PARTICIPANTS);
		}

		/* 채팅방에서 호출된 경우가 아니라면, 채팅방에서 채팅방 삭제 이벤트 발생
		 * 만약 채팅방에서 호출된 경우, 채팅방 삭제 로직이 채팅방 도메인에서 이미 실행됐으므로 삭제 이벤트 호출 X*/

		if (!isPublishedByChatRoom) {
			eventPublisher.publishEvent(new HostMeetingDeleteEvent(meeting.getChatRoom().getId(), userDetails));
		}

		meeting.deleteMeeting();
	}

	//채팅방 메소드에서 호출할 모임 참가자의 모임 나가기 메소드
	@Transactional
	public void participantMeetingDelete(Long meetingId, UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);
		Meeting meeting = meetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		MeetingParticipant meetingParticipant = meetingParticipantRepository.findByMeetingAndMember(meeting, member)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		meetingParticipantRepository.delete(meetingParticipant);
	}

	// 오프라인 모임 수정
	@Transactional
	public void updateOfflineMeeting(Long meetingId, UpdateOfflineMeetingRequestDto updateOfflineMeetingRequestDto,
		UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);

		Image backgroundImage = Optional.ofNullable(updateOfflineMeetingRequestDto.getBackgroundImage())
			.map(image -> imageSaveService.uploadImage(image, MEETING_BACKGROUND))
			.orElse(null);

		OfflineMeeting offlineMeeting = offlineMeetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		if (meetingParticipantRepository.findByMeetingAndMember(offlineMeeting, member).equals(PARTICIPANT)) {
			throw new CommonException(ErrorCode.NOT_MEETING_HOST);
		}

		offlineMeeting.updateOfflineMeeting(
			updateOfflineMeetingRequestDto.getMeetingName(),
			updateOfflineMeetingRequestDto.getMeetingDescription(),
			updateOfflineMeetingRequestDto.getMeetingPlace(),
			updateOfflineMeetingRequestDto.getMeetingDate(),
			updateOfflineMeetingRequestDto.getOfflineMeetingCategory(),
			backgroundImage
		);
	}

	// 배달 모임 수정
	@Transactional
	public void updateDeliveryMeeting(Long meetingId, UpdateDeliveryMeetingRequestDto updateDeliveryMeetingRequestDto,
		UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);

		Image backgroundImage = Optional.ofNullable(updateDeliveryMeetingRequestDto.getBackgroundImage())
			.map(image -> imageSaveService.uploadImage(image, MEETING_BACKGROUND))
			.orElse(null);

		DeliveryMeeting deliveryMeeting = deliveryMeetingRepository.findById(meetingId)
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		if (meetingParticipantRepository.findByMeetingAndMember(deliveryMeeting, member).equals(PARTICIPANT)) {
			throw new CommonException(ErrorCode.NOT_MEETING_HOST);
		}

		deliveryMeeting.updateDeliveryMeeting(
			updateDeliveryMeetingRequestDto.getMeetingName(),
			updateDeliveryMeetingRequestDto.getMeetingDescription(),
			updateDeliveryMeetingRequestDto.getFoodCategory(),
			updateDeliveryMeetingRequestDto.getStoreName(),
			updateDeliveryMeetingRequestDto.getPickupLocation(),
			updateDeliveryMeetingRequestDto.getAccountNumber(),
			updateDeliveryMeetingRequestDto.getBankName(),
			backgroundImage
		);
	}

}

