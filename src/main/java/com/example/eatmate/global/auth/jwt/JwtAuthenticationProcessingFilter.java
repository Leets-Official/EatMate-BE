package com.example.eatmate.global.auth.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.Role;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 인증 필터 (헤더 기반으로 변경)
 *
 * 1. RefreshToken이 있으면 DB에서 검증 후 AccessToken + RefreshToken 재발급 (RTR 방식)
 * 2. RefreshToken이 없으면 AccessToken을 검증하여 인증 처리
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private static final String NO_CHECK_URL = "/login"; // "/login" 요청은 필터 제외

	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		log.info("Processing request URI: {}", request.getRequestURI());

		// "/login" 요청은 필터 제외
		if (request.getRequestURI().equals(NO_CHECK_URL)) {
			filterChain.doFilter(request, response);
			return;
		}
		// ✅ OAuth2 로그인 요청은 JWT 인증 제외
		if (requestURI.startsWith("/api/auth/google")) {
			log.info("OAuth2 로그인 요청 감지, JWT 인증 필터 적용 제외: {}", requestURI);
			filterChain.doFilter(request, response);
			return;
		}

		// WebSocket 요청 필터 제외
		if (request.getRequestURI().startsWith("/ws/chat")) {
			log.info("WebSocket 핸드셰이크 요청 감지: 필터를 통과시킴 : {}", request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}

		// Refresh Token 검사
		String refreshToken = jwtService.extractRefreshToken(request)
			.filter(jwtService::isTokenValid)
			.orElse(null);

		if (refreshToken != null) {
			log.info("Valid RefreshToken found. Processing token renewal...");
			handleRefreshToken(response, refreshToken);
			return; // Refresh Token을 재발급 후 인증은 처리하지 않음
		}

		//  RefreshToken이 없으면 Access Token 검사
		checkAccessTokenAndAuthenticate(request, response, filterChain);
	}

	/**
	 * [리프레시 토큰 확인 후 Access Token + Refresh Token 재발급]
	 */
	private void handleRefreshToken(HttpServletResponse response, String refreshToken) {
		memberRepository.findByRefreshToken(refreshToken)
			.ifPresentOrElse(
				member -> {
					// 새 Access Token & Refresh Token 발급
					String newAccessToken = jwtService.createAccessToken(member.getEmail(), member.getRole().name(),
						member.getRole() == Role.USER ? member.getGender().name() : null);
					String newRefreshToken = jwtService.createRefreshToken();

					// DB에 새 Refresh Token 저장
					member.updateRefreshToken(newRefreshToken);
					memberRepository.saveAndFlush(member);

					// 응답 헤더에 토큰 추가
					jwtService.sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);

					log.info("Refresh Token 유효. Access Token 및 Refresh Token 재발급 완료.");
				},
				() -> sendErrorResponse(response, "Invalid Refresh Token")
			);
	}

	/**
	 * [Access Token 검사 및 인증 수행]
	 */
	private void checkAccessTokenAndAuthenticate(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		jwtService.extractAccessToken(request)
			.filter(jwtService::isTokenValid) // AccessToken 유효성 검증
			.flatMap(jwtService::extractEmail) // 유효한 AccessToken에서 Email 추출
			.flatMap(memberRepository::findByEmail) // 이메일로 Member 조회
			.ifPresentOrElse(
				member -> {
					log.info("Authentication successful for user: {}", member.getEmail());
					saveAuthentication(member); // SecurityContext에 인증 정보 저장
				},
				() -> log.warn("Failed to authenticate user. Either token is invalid or member not found.")
			);

		// 필터 체인 계속 진행
		filterChain.doFilter(request, response);
	}

	/**
	 * [SecurityContext에 인증 정보 저장]
	 */
	private void saveAuthentication(Member member) {
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
			.username(member.getEmail())
			.password("") // 비밀번호는 사용하지 않으므로 빈 문자열
			.roles(member.getRole().name())
			.build();

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities())
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * [에러 응답 전송]
	 */
	private void sendErrorResponse(HttpServletResponse response, String message) {
		try {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setHeader("Error-Message", message); // 에러 메시지를 헤더에 설정
			log.warn("에러 응답: {}", message);
		} catch (Exception e) {
			log.error("에러 응답 전송 중 오류 발생", e);
		}
	}
}
