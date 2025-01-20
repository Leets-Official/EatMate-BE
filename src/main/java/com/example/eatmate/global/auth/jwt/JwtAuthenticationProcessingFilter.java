package com.example.eatmate.global.auth.jwt;

import java.io.IOException;
import java.util.Arrays;

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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 *
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private static final String NO_CHECK_URL = "/login"; // 로그인 요청은 필터 제외

	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		log.info("Processing request URI: {}", request.getRequestURI());

		if (request.getRequestURI().equals(NO_CHECK_URL)) {
			filterChain.doFilter(request, response); // /login 요청은 필터 제외
			return;
		}

		// 요청에서 쿠키 추출 및 디버깅 로그
		log.info("Request Cookies: {}", Arrays.toString(request.getCookies()));

		// Refresh Token 처리
		String refreshToken = jwtService.extractRefreshTokenFromCookie(request)
			.filter(jwtService::isTokenValid)
			.orElse(null);

		if (refreshToken != null) {
			log.info("Valid RefreshToken found. Processing token renewal...");
			handleRefreshToken(response, refreshToken);
		}

		// Access Token 처리
		try {
			handleAccessToken(request, response, filterChain);
		} catch (Exception e) {
			log.error("Error while handling Access Token: {}", e.getMessage(), e);
			sendErrorResponse(response, "Invalid Access Token");
		}
	}

	/**
	 * Refresh Token 처리 및 Access Token 재발급
	 */
	private void handleRefreshToken(HttpServletResponse response, String refreshToken) {
		memberRepository.findByRefreshToken(refreshToken)
			.ifPresentOrElse(
				member -> {
					String newAccessToken = jwtService.createAccessToken(member.getEmail(), member.getRole().name(),
						member.getRole() == Role.USER ? member.getGender().name() : null);
					String newRefreshToken = jwtService.createRefreshToken();
					member.updateRefreshToken(newRefreshToken);
					memberRepository.saveAndFlush(member);

					// 쿠키에 토큰 설정
					setTokenInCookie(response, "AccessToken", newAccessToken,
						jwtService.getAccessTokenExpirationPeriod());
					setTokenInCookie(response, "RefreshToken", newRefreshToken,
						jwtService.getRefreshTokenExpirationPeriod());

					log.info("Refresh Token 유효. Access Token 및 Refresh Token 재발급 완료.");
				},
				() -> sendErrorResponse(response, "Invalid Refresh Token")
			);
	}

	/**
	 * Access Token 처리 및 SecurityContext 설정
	 */
	private void handleAccessToken(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		jwtService.extractAccessTokenFromCookie(request)
			.filter(jwtService::isTokenValid) // AccessToken 유효성 검증
			.flatMap(jwtService::extractEmail) // 유효한 AccessToken에서 Email 추출
			.flatMap(memberRepository::findByEmail) // 이메일로 Member 조회
			.ifPresentOrElse(
				member -> {
					log.info("Authentication successful for user: {}", member.getEmail());
					setAuthentication(member); // 인증 정보 SecurityContext에 저장
				},
				() -> log.warn("Failed to authenticate user. Either token is invalid or member not found.")
			);

		filterChain.doFilter(request, response);
	}

	/**
	 * 인증 정보 SecurityContext에 저장
	 */
	private void setAuthentication(Member member) {
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
	 * 에러 응답 전송
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

	/**
	 * 쿠키에 토큰 설정
	 */
	private void setTokenInCookie(HttpServletResponse response, String name, String token, long maxAge) {
		Cookie cookie = new Cookie(name, token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge((int)(maxAge / 1000)); // maxAge는 초 단위로 설정
		response.addCookie(cookie);
		log.info("{} 쿠키 설정 완료", name);
	}
}
