package com.example.eatmate.app.domain.block.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.block.dto.BlockIdResponseDto;
import com.example.eatmate.app.domain.block.dto.BlockMemberRequestDto;
import com.example.eatmate.app.domain.block.dto.BlockedMemberListResponseDto;
import com.example.eatmate.app.domain.block.dto.UnblockMemberRequestDto;
import com.example.eatmate.app.domain.block.service.BlockService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/block")
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	@GetMapping("/member")
	@Operation(summary = "차단한 유저를 조회", description = "차단한 유저를 조회합니다.")
	public ResponseEntity<GlobalResponseDto<List<BlockedMemberListResponseDto>>> getMyMemberBlock(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(blockService.getMyBlockMember(userDetails)));
	}

	@PostMapping("/member")
	@Operation(summary = "유저를 차단합니다", description = "선택한 유저를 차단합니다.")
	public ResponseEntity<GlobalResponseDto<BlockIdResponseDto>> createMemberBlock(
		@RequestBody @Valid BlockMemberRequestDto blockMemberRequestDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success(blockService.blockMember(userDetails, blockMemberRequestDto)));
	}

	@DeleteMapping("/member")
	@Operation(summary = "차단 해제합니다", description = "선택한 유저를 차단 해제합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> deleteMemberBlock(
		@RequestBody @Valid UnblockMemberRequestDto unblockMemberRequestDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		blockService.unblockMember(userDetails, unblockMemberRequestDto);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}

}
