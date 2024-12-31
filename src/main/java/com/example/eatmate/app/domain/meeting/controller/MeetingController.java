package com.example.eatmate.app.domain.meeting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingRequestDto;
import com.example.eatmate.app.domain.meeting.dto.CreateDeliveryMeetingResponseDto;
import com.example.eatmate.app.domain.meeting.service.MeetingService;
import com.example.eatmate.global.auth.login.service.CustomUserDetails;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {
	private final MeetingService meetingService;

	@PostMapping("/delivery")
	@Operation(summary = "배달 모임 생성", description = "배달 모임을 생성합니다.")
	public ResponseEntity<GlobalResponseDto<CreateDeliveryMeetingResponseDto>> getProfile(
		@RequestBody CreateDeliveryMeetingRequestDto createDeliveryMeetingRequestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success(meetingService.createDeliveryMeeting(createDeliveryMeetingRequestDto, userDetails.getMemberId()),
				HttpStatus.CREATED.value()));
	}
}
