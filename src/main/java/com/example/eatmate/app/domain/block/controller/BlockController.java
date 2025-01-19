package com.example.eatmate.app.domain.block.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.block.dto.BlockIdResponseDto;
import com.example.eatmate.app.domain.block.dto.CreateMeetingBlockDto;
import com.example.eatmate.app.domain.block.dto.CreateMemberBlockDto;
import com.example.eatmate.app.domain.block.service.BlockService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/block")
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	@PostMapping("/meeting")
	@Operation(summary = "모임을 차단합니다", description = "선택한 모임을 차단합니다.")
	public ResponseEntity<GlobalResponseDto<BlockIdResponseDto>> createMeetingBlock(
		@RequestBody CreateMeetingBlockDto createMeetingBlockDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(
				GlobalResponseDto.success(blockService.blockMeeting(userDetails, createMeetingBlockDto)));
	}

	@PostMapping("/member")
	@Operation(summary = "유저를 차단합니다", description = "선택한 유저를 차단합니다.")
	public ResponseEntity<GlobalResponseDto<BlockIdResponseDto>> createMemberBlock(
		@RequestBody CreateMemberBlockDto createMemberBlockDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(
				GlobalResponseDto.success(blockService.blockMember(userDetails, createMemberBlockDto)));
	}
}
