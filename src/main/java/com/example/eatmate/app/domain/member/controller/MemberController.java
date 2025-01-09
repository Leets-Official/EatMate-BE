package com.example.eatmate.app.domain.member.controller;

import com.example.eatmate.app.domain.member.dto.MyInfoResponseDto;
import com.example.eatmate.global.response.GlobalResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.eatmate.app.domain.member.dto.MemberLoginRequestDto;
import com.example.eatmate.app.domain.member.dto.MemberLoginResponseDto;
import com.example.eatmate.app.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
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
	public ResponseEntity<GlobalResponseDto<MyInfoResponseDto>> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
		// 서비스 호출 및 응답 반환
		return ResponseEntity.ok(
				GlobalResponseDto.success(memberService.getMyInfo(userDetails), HttpStatus.OK.value())
		);
	}
}
