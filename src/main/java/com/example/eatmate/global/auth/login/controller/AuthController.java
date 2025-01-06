package com.example.eatmate.global.auth.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.member.dto.MemberSignUpRequestDto;
import com.example.eatmate.app.domain.member.service.MemberService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final MemberService memberService;

	//회원가입
	@PostMapping("/signup") //매핑 경로 수정
	@Operation(summary = "회원가입", description = "회원가입을 합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> register(
		@RequestBody @Valid MemberSignUpRequestDto memberSignUpRequestDto,
		@AuthenticationPrincipal UserDetails userDetails) {

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success(memberService.completeRegistration(memberSignUpRequestDto, userDetails),
				HttpStatus.CREATED.value()));
	}

	//로그아웃
}
