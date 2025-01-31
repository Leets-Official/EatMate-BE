package com.example.eatmate.app.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.eatmate.app.domain.member.dto.MemberLoginRequestDto;
import com.example.eatmate.app.domain.member.dto.MemberLoginResponseDto;
import com.example.eatmate.app.domain.member.dto.MyInfoResponseDto;
import com.example.eatmate.app.domain.member.dto.MyInfoUpdateRequestDto;
import com.example.eatmate.app.domain.member.dto.UserInfoResponseDto;
import com.example.eatmate.app.domain.member.service.MemberService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class MemberController {

	//추후 구현 예정
	private final MemberService memberService;

	//개발용 임시 로그인/회원가입
	@PostMapping("/login")
	@Operation(summary = "로그인", description = "개발전용 임시 로그인/회원가입 API")
	public MemberLoginResponseDto login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
		return memberService.login(memberLoginRequestDto);

	}

	@GetMapping("/myinfo")
	@Operation(summary = "본인 정보 조회", description = "마이페이지에 들어올 시 조회되는 본인의 정보")
	public ResponseEntity<GlobalResponseDto<MyInfoResponseDto>> getMyInfo(
		@AuthenticationPrincipal UserDetails userDetails) {
		// 서비스 호출 및 응답 반환
		return ResponseEntity.ok(
			GlobalResponseDto.success(memberService.getMyInfo(userDetails))
		);
	}

	@GetMapping("/info/{memberId}")
	@Operation(summary = "상대방 정보 조회", description = "상대방 프로필 정보를 조회합니다.")
	public ResponseEntity<GlobalResponseDto<UserInfoResponseDto>> getProfileInfo(
		@PathVariable Long memberId) {
		return ResponseEntity.ok(
			GlobalResponseDto.success(memberService.getProfileInfo(memberId))
		);
	}

	@PatchMapping("/myinfo")
	@Operation(summary = "프로필 수정", description = "사용자의 닉네임, 전화번호, MBTI, 생년월일을 일부 수정합니다.")
	public ResponseEntity<GlobalResponseDto<MyInfoResponseDto>> updateMyProfile(
		@RequestPart(value = "data", required = false) @Valid MyInfoUpdateRequestDto updateRequestDto,
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		return ResponseEntity.ok(
			GlobalResponseDto.success(memberService.updateMyInfo(userDetails, updateRequestDto, profileImage))
		);
	}
}
