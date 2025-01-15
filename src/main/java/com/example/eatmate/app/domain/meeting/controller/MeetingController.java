package com.example.eatmate.app.domain.meeting.controller;

import static com.example.eatmate.app.domain.meeting.domain.ParticipantRole.*;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingSortType;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.DeliveryMeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.DeliveryMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MyMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.OfflineMeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpcomingMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.service.MeetingService;
import com.example.eatmate.global.response.CursorResponseDto;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {
	private final MeetingService meetingService;

	/**
	 * QueryString으로 들어오는 MeetingSortType enum 값을 대소문자 구분 없이 처리하기 위한 설정
	 *
	 * 사용 예시:
	 * /api/meetings?sort-type=created_at
	 *
	 * 변환 과정:
	 * 1. created_at (클라이언트 요청)
	 * 2. CREATED_AT (toUpperCase 변환)
	 * 3. MeetingSortType.CREATED_AT (Enum 매핑)
	 */

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.registerCustomEditor(MeetingSortType.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(MeetingSortType.valueOf(text.toUpperCase()));
			}
		});
	}

	@PostMapping("/delivery")
	@Operation(summary = "배달 모임 생성", description = "배달 모임을 생성합니다.")
	public ResponseEntity<GlobalResponseDto<CreateDeliveryMeetingResponseDto>> createDeliveryMeeting(
		@RequestBody @Valid CreateDeliveryMeetingRequestDto createDeliveryMeetingRequestDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success(
				meetingService.createDeliveryMeeting(createDeliveryMeetingRequestDto, userDetails),
				HttpStatus.CREATED.value()));
	}

	@PostMapping("/offline")
	@Operation(summary = "오프라인 모임 생성", description = "오프라인 모임을 생성합니다.")
	public ResponseEntity<GlobalResponseDto<CreateOfflineMeetingResponseDto>> createOfflineMeeting(
		@RequestBody @Valid CreateOfflineMeetingRequestDto createOfflineMeetingRequestDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success(
				meetingService.createOfflineMeeting(createOfflineMeetingRequestDto, userDetails),
				HttpStatus.CREATED.value()));
	}

	@PostMapping("/{meetingId}/delivery")
	@Operation(summary = "배달 모임 참가", description = "배달 모임에 참가합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> joinDeliveryMeeting(
		@PathVariable Long meetingId,
		@AuthenticationPrincipal UserDetails userDetails) {
		meetingService.joinDeliveryMeeting(meetingId, userDetails);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}

	@PostMapping("/{meetingId}/offline")
	@Operation(summary = "오프라인 모임 참가", description = "오프라인 모임에 참가합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> joinOfflineMeeting(
		@PathVariable Long meetingId,
		@AuthenticationPrincipal UserDetails userDetails) {
		meetingService.joinOfflineMeeting(meetingId, userDetails);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}

	@GetMapping("/offline")
	@Operation(summary = "오프라인 모임 목록 조회", description = "오프라인 모임 목록을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<List<MeetingListResponseDto>>> getOfflineMeetingList(
		@RequestParam(required = true) OfflineMeetingCategory category,
		// @RequestParam(defaultValue = "0")
		// @PositiveOrZero(message = "페이지 번호는 0 이상이어야 합니다") int page,
		@RequestParam(defaultValue = "20")
		@Positive(message = "페이지 크기는 양수여야 합니다")
		@Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다") Long pageSize,
		@RequestParam(value = "gender-restriction", required = false) GenderRestriction genderRestriction,
		@RequestParam(value = "max-participant", required = false) Long maxParticipant,
		@RequestParam(value = "min-participant", required = false) Long minParticipant,
		@RequestParam(value = "sort-type", required = false) MeetingSortType sortType) {
		// PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getOfflineMeetingList(category, genderRestriction, maxParticipant, minParticipant,
					sortType, pageSize)));
	}

	@GetMapping("/delivery")
	@Operation(summary = "배달 모임 목록 조회", description = "배달 모임 목록을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<Slice<DeliveryMeetingListResponseDto>>> getDeliveryMeetingList(
		@RequestParam(required = true) FoodCategory foodCategory,
		@RequestParam(defaultValue = "0")
		@PositiveOrZero(message = "페이지 번호는 0 이상이어야 합니다") int page,
		@RequestParam(defaultValue = "20")
		@Positive(message = "페이지 크기는 양수여야 합니다")
		@Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다") int size) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getDeliveryMeetingList(foodCategory, pageRequest)));
	}

	@GetMapping("/offline/{meetingId}")
	@Operation(summary = "오프라인 모임 상세 조회", description = "오프라인 모임 상세 정보를 조회합니다.")
	public ResponseEntity<GlobalResponseDto<OfflineMeetingDetailResponseDto>> getOfflineMeetingDetail(
		@PathVariable Long meetingId) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getOfflineMeetingDetail(meetingId)));
	}

	@GetMapping("/delivery/{meetingId}")
	@Operation(summary = "배달 모임 상세 조회", description = "배달 모임 상세 정보를 조회합니다.")
	public ResponseEntity<GlobalResponseDto<DeliveryMeetingDetailResponseDto>> getDeliveryMeetingDetail(
		@PathVariable Long meetingId) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getDeliveryMeetingDetail(meetingId)));
	}

	@GetMapping("/my/created")
	@Operation(summary = "내가 생성한 모임 목록 조회", description = "마이페이지에서 내가 생성한 모임 목록(과거 포함)을 조회합니다.")
	@Parameter(name = "lastMeetingId", description = "마지막으로 조회한 모임 ID", required = false)
	@Parameter(name = "lastDateTime", description = "마지막으로 조회한 모임의 날짜시간(ISO 형식: yyyy-MM-dd'T'HH:mm:ss)", required = false)
	@Parameter(name = "pageSize", description = "페이지당 조회할 항목 수 (기본값: 20, 최대: 100)", required = false)
	public ResponseEntity<GlobalResponseDto<CursorResponseDto<MyMeetingListResponseDto>>> getMyCreatedMeetingList(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(required = false) Long lastMeetingId,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDateTime,
		@RequestParam(defaultValue = "20")
		@Positive(message = "페이지 크기는 양수여야 합니다")
		@Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다") int pageSize
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getMyMeetingList(userDetails, HOST, lastMeetingId, lastDateTime, pageSize)));
	}

	@GetMapping("/my/participated")
	@Operation(summary = "내가 참여한 모임 목록 조회", description = "마이페이지에서 내가 참여한 모임 목록(과거 포함)을 조회합니다.")
	@Parameter(name = "lastMeetingId", description = "마지막으로 조회한 모임 ID", required = false)
	@Parameter(name = "lastDateTime", description = "마지막으로 조회한 모임의 날짜시간(ISO 형식: yyyy-MM-dd'T'HH:mm:ss)", required = false)
	@Parameter(name = "pageSize", description = "페이지당 조회할 항목 수 (기본값: 20, 최대: 100)", required = false)
	public ResponseEntity<GlobalResponseDto<CursorResponseDto<MyMeetingListResponseDto>>> getMyParticipatedMeetingList(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(required = false) Long lastMeetingId,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDateTime,
		@RequestParam(defaultValue = "20")
		@Positive(message = "페이지 크기는 양수여야 합니다")
		@Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다") int pageSize
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getMyMeetingList(userDetails, PARTICIPANT, lastMeetingId, lastDateTime, pageSize)));
	}

	@GetMapping("/my/participating")
	@Operation(summary = "내가 참여 중인 모임 목록 조회", description = "내가 참여중인 활성화된 모임 목록을 조회합니다.")
	@Parameter(name = "lastMeetingId", description = "마지막으로 조회한 모임 ID", required = false)
	@Parameter(name = "lastDateTime", description = "마지막으로 조회한 모임의 날짜시간(ISO 형식: yyyy-MM-dd'T'HH:mm:ss)", required = false)
	@Parameter(name = "pageSize", description = "페이지당 조회할 항목 수 (기본값: 20, 최대: 100)", required = false)
	public ResponseEntity<GlobalResponseDto<CursorResponseDto<MyMeetingListResponseDto>>> getMyActiveMeetingList(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(required = false) Long lastMeetingId,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDateTime,
		@RequestParam(defaultValue = "20")
		@Positive(message = "페이지 크기는 양수여야 합니다")
		@Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다") int pageSize
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getMyActiveMeetingList(userDetails, lastMeetingId, lastDateTime, pageSize)));
	}

	@GetMapping("/my/upcoming")
	@Operation(summary = "가장 임박한 모임 조회", description = "내가 참여중인 활성화된 모임 중 가장 임박한 모임을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<UpcomingMeetingResponseDto>> getUpcomingMeeting(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getUpcomingMeeting(userDetails)));
	}

}
