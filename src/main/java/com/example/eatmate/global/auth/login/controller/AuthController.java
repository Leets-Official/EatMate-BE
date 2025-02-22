package com.example.eatmate.global.auth.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.eatmate.app.domain.member.dto.MemberSignUpRequestDto;
import com.example.eatmate.app.domain.member.service.MemberService;
import com.example.eatmate.global.auth.jwt.JwtService;
import com.example.eatmate.global.auth.login.dto.UserLoginResponseDto;
import com.example.eatmate.global.auth.login.service.LoginService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final MemberService memberService;
	private final JwtService jwtService;
	private final LoginService loginService;

	@PostMapping("/signup")
	@Operation(summary = "회원가입", description = "회원가입을 합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> register(
		@RequestPart(value = "data") @Valid MemberSignUpRequestDto memberSignUpRequestDto,
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
		@AuthenticationPrincipal UserDetails userDetails) {

		log.info("Received profileImage in controller: {}",
			profileImage != null ? profileImage.getOriginalFilename() : "null");

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success(
				memberService.completeRegistration(memberSignUpRequestDto, profileImage, userDetails),
				HttpStatus.CREATED.value()));
	}

	//로그인 시, 사용자 정보 조회
	@GetMapping("/info")
	@Operation(summary = "사용자 정보 조회", description = "로그인 기반으로 사용자 정보를 조회합니다.")
	public ResponseEntity<GlobalResponseDto<UserLoginResponseDto>> getUserInfo(HttpServletRequest request) {

		UserLoginResponseDto userInfo = loginService.getUserInfoFromRequest(request);

		return ResponseEntity.ok(GlobalResponseDto.success(userInfo, HttpStatus.OK.value()));
	}
}
