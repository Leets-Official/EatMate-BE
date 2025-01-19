package com.example.eatmate.app.domain.block.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.block.dto.BlockIdResponseDto;
import com.example.eatmate.app.domain.block.dto.BlockMeetingResponseDto;
import com.example.eatmate.app.domain.block.dto.BlockMemberResponseDto;
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

	@GetMapping("/meeting")
	@Operation(summary = "차단한 모임을 조회", description = "차단한 모임을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<List<BlockMeetingResponseDto>>> getMyMeetingBlock(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(
				GlobalResponseDto.success(blockService.getMyBlockMeeting(userDetails)));
	}

	@PostMapping("/meeting")
	@Operation(summary = "모임을 차단합니다", description = "선택한 모임을 차단합니다.")
	public ResponseEntity<GlobalResponseDto<BlockIdResponseDto>> createMeetingBlock(
		@RequestBody CreateMeetingBlockDto createMeetingBlockDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(
				GlobalResponseDto.success(blockService.blockMeeting(userDetails, createMeetingBlockDto)));
	}

	@GetMapping("/member")
	@Operation(summary = "차단한 유저를 조회", description = "차단한 유저를 조회합니다.")
	public ResponseEntity<GlobalResponseDto<List<BlockMemberResponseDto>>> getMyMemberBlock(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(
				GlobalResponseDto.success(blockService.getMyBlockMember(userDetails)));
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
