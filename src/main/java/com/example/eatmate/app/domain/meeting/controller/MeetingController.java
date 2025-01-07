package com.example.eatmate.app.domain.meeting.controller;

import static com.example.eatmate.app.domain.meeting.domain.ParticipantRole.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateOfflineMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.dto.DeliveryMeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.DeliveryMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.OfflineMeetingDetailResponseDto;
import com.example.eatmate.app.domain.meeting.dto.OfflineMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.service.MeetingService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {
	private final MeetingService meetingService;

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
	public ResponseEntity<GlobalResponseDto<List<OfflineMeetingListResponseDto>>> getOfflineMeetingList(
		@RequestParam(required = true) OfflineMeetingCategory category) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getOfflineMeetingList(category)));
	}

	@GetMapping("/delivery")
	@Operation(summary = "배달 모임 목록 조회", description = "배달 모임 목록을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<List<DeliveryMeetingListResponseDto>>> getDeliveryMeetingList(
		@RequestParam(required = true) FoodCategory foodCategory) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getDeliveryMeetingList(foodCategory)));
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
	public ResponseEntity<GlobalResponseDto<List<MeetingListResponseDto>>> getMyCreatedMeetingList(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getMyMeetingList(userDetails, HOST)));
	}

	@GetMapping("/my/participated")
	@Operation(summary = "내가 참여한 모임 목록 조회", description = "마이페이지에서 내가 참여한 모임 목록(과거 포함)을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<List<MeetingListResponseDto>>> getMyParticipatedMeetingList(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getMyMeetingList(userDetails, PARTICIPANT)));
	}

	@GetMapping("my/participating")
	@Operation(summary = "내가 참여 중인 모임 목록 조회", description = "내가 참여중인 활성화된 모임 목록을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<List<MeetingListResponseDto>>>
	getMyActiveMeetingList(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getMyActiveMeetingList(userDetails, null)));
	}

}
