package com.example.eatmate.app.domain.member.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
