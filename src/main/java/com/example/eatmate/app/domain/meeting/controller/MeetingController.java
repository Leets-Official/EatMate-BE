package com.example.eatmate.app.domain.meeting.controller;

import static com.example.eatmate.app.domain.meeting.domain.ParticipantRole.*;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.example.eatmate.app.domain.meeting.dto.MeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MyMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpcomingMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpdateDeliveryMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.UpdateOfflineMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.service.MeetingService;
import com.example.eatmate.global.response.CursorResponseDto;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {
	private final MeetingService meetingService;

	@PostMapping("/delivery")
	@Operation(summary = "배달 모임 생성", description = "배달 모임을 생성합니다.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
		mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
		schema = @Schema(implementation = CreateDeliveryMeetingRequestDto.class)))
	public ResponseEntity<GlobalResponseDto<CreateDeliveryMeetingResponseDto>> createDeliveryMeeting(
		@ModelAttribute @Valid
		@Parameter(
			description = "배달 모임 생성 정보",
			content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
		)
		CreateDeliveryMeetingRequestDto createDeliveryMeetingRequestDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success(
				meetingService.createDeliveryMeeting(createDeliveryMeetingRequestDto, userDetails),
				HttpStatus.CREATED.value()));
	}

	@PostMapping("/offline")
	@Operation(summary = "오프라인 모임 생성", description = "오프라인 모임을 생성합니다.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
		mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
		schema = @Schema(implementation = CreateDeliveryMeetingRequestDto.class)))
	public ResponseEntity<GlobalResponseDto<CreateOfflineMeetingResponseDto>> createOfflineMeeting(
		@ModelAttribute @Valid CreateOfflineMeetingRequestDto createOfflineMeetingRequestDto,
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
	public ResponseEntity<GlobalResponseDto<CursorResponseDto>> getOfflineMeetingList(
		@RequestParam(required = true) OfflineMeetingCategory category,
		@RequestParam(value = "page-size", defaultValue = "20")
		@Positive(message = "페이지 크기는 양수여야 합니다")
		@Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다") int pageSize,
		@RequestParam(value = "gender-restriction", required = false) GenderRestriction genderRestriction,
		@RequestParam(value = "max-participant", required = false) Long maxParticipant,
		@RequestParam(value = "min-participant", required = false) Long minParticipant,
		@RequestParam(value = "sort-type", required = true) MeetingSortType sortType,
		@RequestParam(value = "last-meeting-id", required = false) Long lastMeetingId,
		@RequestParam(value = "last-date-time", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDateTime) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getOfflineMeetingList(category, genderRestriction, maxParticipant, minParticipant,
					sortType, pageSize, lastMeetingId, lastDateTime)));
	}

	@GetMapping("/delivery")
	@Operation(summary = "배달 모임 목록 조회", description = "배달 모임 목록을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<CursorResponseDto>> getDeliveryMeetingList(
		@RequestParam(required = false) FoodCategory category,
		@RequestParam(value = "page-size", defaultValue = "20")
		@Positive(message = "페이지 크기는 양수여야 합니다")
		@Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다") int pageSize,
		@RequestParam(value = "gender-restriction", required = false) GenderRestriction genderRestriction,
		@RequestParam(value = "max-participant", required = false) Long maxParticipant,
		@RequestParam(value = "min-participant", required = false) Long minParticipant,
		@RequestParam(value = "sort-type", required = true) MeetingSortType sortType,
		@RequestParam(value = "last-meeting-id", required = false) Long lastMeetingId,
		@RequestParam(value = "last-date-time", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDateTime) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getDeliveryMeetingList(category, genderRestriction, maxParticipant, minParticipant,
					sortType, pageSize, lastMeetingId, lastDateTime)));
	}

	@GetMapping("/{meetingId}")
	@Operation(summary = "모임 상세 조회", description = "모임 상세 정보를 조회합니다.")
	public ResponseEntity<GlobalResponseDto<MeetingDetailResponseDto>> getOfflineMeetingDetail(
		@PathVariable Long meetingId,
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getMeetingDetail(meetingId, userDetails)));
	}

	@GetMapping("/my/created")
	@Operation(summary = "내가 생성한 모임 목록 조회", description = "마이페이지에서 내가 생성한 모임 목록(과거 포함)을 조회합니다.")
	@Parameter(name = "last-meeting-id", description = "마지막으로 조회한 모임 ID", required = false)
	@Parameter(name = "last-date-time", description = "마지막으로 조회한 모임의 날짜시간(ISO 형식: yyyy-MM-dd'T'HH:mm:ss)", required = false)
	@Parameter(name = "page-size", description = "페이지당 조회할 항목 수 (기본값: 20, 최대: 100)", required = false)
	public ResponseEntity<GlobalResponseDto<CursorResponseDto<MyMeetingListResponseDto>>> getMyCreatedMeetingList(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(value = "last-meeting-id", required = false) Long lastMeetingId,
		@RequestParam(value = "last-date-time", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDateTime,
		@RequestParam(value = "page-size", defaultValue = "20")
		@Positive(message = "페이지 크기는 양수여야 합니다")
		@Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다") int pageSize
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getMyMeetingList(userDetails, HOST, lastMeetingId, lastDateTime, pageSize)));
	}

	@GetMapping("/my/participated")
	@Operation(summary = "내가 참여한 모임 목록 조회", description = "마이페이지에서 내가 참여한 모임 목록(과거 포함)을 조회합니다.")
	@Parameter(name = "last-meeting-id", description = "마지막으로 조회한 모임 ID", required = false)
	@Parameter(name = "last-date-time", description = "마지막으로 조회한 모임의 날짜시간(ISO 형식: yyyy-MM-dd'T'HH:mm:ss)", required = false)
	@Parameter(name = "page-size", description = "페이지당 조회할 항목 수 (기본값: 20, 최대: 100)", required = false)
	public ResponseEntity<GlobalResponseDto<CursorResponseDto<MyMeetingListResponseDto>>> getMyParticipatedMeetingList(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(value = "last-meeting-id", required = false) Long lastMeetingId,
		@RequestParam(value = "last-date-time", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDateTime,
		@RequestParam(value = "page-size", defaultValue = "20")
		@Positive(message = "페이지 크기는 양수여야 합니다")
		@Max(value = 100, message = "페이지 크기는 최대 100을 초과할 수 없습니다") int pageSize
	) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getMyMeetingList(userDetails, PARTICIPANT, lastMeetingId, lastDateTime, pageSize)));
	}

	@GetMapping("/my/participating")
	@Operation(summary = "내가 참여 중인 모임 목록 조회", description = "내가 참여중인 활성화된 모임 목록을 조회합니다.")
	@Parameter(name = "last-meeting-id", description = "마지막으로 조회한 모임 ID", required = false)
	@Parameter(name = "last-date-time", description = "마지막으로 조회한 모임의 날짜시간(ISO 형식: yyyy-MM-dd'T'HH:mm:ss)", required = false)
	@Parameter(name = "page-size", description = "페이지당 조회할 항목 수 (기본값: 20, 최대: 100)", required = false)
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

	@PatchMapping("/{meetingId}")
	@Operation(summary = "모임 삭제", description = "모임을 삭제합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> deleteMeeting(
		@PathVariable Long meetingId,
		@AuthenticationPrincipal UserDetails userDetails) {
		meetingService.hostMeetingDelete(meetingId, userDetails, false);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}

	@PatchMapping("/{meetingId}/offline")
	@Operation(summary = "오프라인 모임 수정", description = "오프라인 모임을 수정합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> updateOfflineMeeting(
		@PathVariable Long meetingId,
		@ModelAttribute @Valid UpdateOfflineMeetingRequestDto updateOfflineMeetingRequestDto,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		meetingService.updateOfflineMeeting(meetingId, updateOfflineMeetingRequestDto, userDetails);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}

	@PatchMapping("/{meetingId}/delivery")
	@Operation(summary = "배달 모임 수정", description = "배달 모임을 수정합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> updateDeliveryMeeting(
		@PathVariable Long meetingId,
		@RequestBody @Valid UpdateDeliveryMeetingRequestDto UpdateDeliveryMeetingRequestDto,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		meetingService.updateDeliveryMeeting(meetingId, UpdateDeliveryMeetingRequestDto, userDetails);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}
}
