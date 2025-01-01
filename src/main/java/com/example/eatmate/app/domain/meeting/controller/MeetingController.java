package com.example.eatmate.app.domain.meeting.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.eatmate.app.domain.meeting.dto.DeliveryMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.OfflineMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.service.MeetingService;
import com.example.eatmate.global.auth.login.service.CustomUserDetails;
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
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success(
				meetingService.createDeliveryMeeting(createDeliveryMeetingRequestDto, userDetails.getMemberId()),
				HttpStatus.CREATED.value()));
	}


	@PostMapping("/{meetingId}/delivery")
	@Operation(summary = "모임 참가", description = "모임에 참가합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> joinDeliveryMeeting(
		@PathVariable Long meetingId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		meetingService.joinMeeting(meetingId, userDetails.getMemberId());
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}

	@GetMapping("/offline")
	@Operation(summary = "오프라인 모임 목록 조회", description = "오프라인 모임 목록을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<List<OfflineMeetingListResponseDto>>> getOfflineMeetingList(@RequestParam(required = true) OfflineMeetingCategory category) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getOfflineMeetingList(category)));
	}

	@GetMapping("/delivery")
	@Operation(summary = "배달 모임 목록 조회", description = "배달 모임 목록을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<List<DeliveryMeetingListResponseDto>>> getDeliveryMeetingList(@RequestParam(required = true) FoodCategory foodCategory) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(
				meetingService.getDeliveryMeetingList(foodCategory)));
	}
}
