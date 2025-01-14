package com.example.eatmate.global.auth.login.service;

import static org.springframework.security.core.userdetails.User.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.Role;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.auth.jwt.JwtService;
import com.example.eatmate.global.auth.login.dto.UserLoginResponseDto;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;
import com.example.eatmate.global.config.error.exception.custom.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final JwtService jwtService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(UserNotFoundException::new);

		return builder()
			.username(member.getEmail())
			.build();
	}

	public UserLoginResponseDto getUserInfoFromRequest(HttpServletRequest request) {
		// 쿠키에서 AccessToken 추출
		String accessToken = jwtService.extractAccessTokenFromCookie(request)
			.orElseThrow(() -> new CommonException(ErrorCode.TOKEN_NOT_FOUND));

		// AccessToken 유효성 검증 및 사용자 정보 조회
		return getUserInfo(accessToken);
	}

	public UserLoginResponseDto getUserInfo(String accessToken) {
		// AccessToken 유효성 검증
		if (!jwtService.isTokenValid(accessToken)) {
			throw new CommonException(ErrorCode.INVALID_TOKEN);
		}
		// AccessToken에서 이메일과 역할(Role) 추출
		String email = jwtService.extractEmail(accessToken)
			.orElseThrow(() -> new CommonException(ErrorCode.INVALID_TOKEN));
		String role = jwtService.extractRole(accessToken)
			.orElseThrow(() -> new CommonException(ErrorCode.INVALID_TOKEN));

		// 사용자 정보 반환
		return new UserLoginResponseDto(email, Role.valueOf(role));
	}

}
