package com.example.eatmate.global.auth.login.oauth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.eatmate.app.domain.member.domain.Role;
import com.example.eatmate.global.auth.jwt.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

	private static final boolean COOKIE_HTTP_ONLY = true;
	private static final boolean COOKIE_SECURE = true; // https 환경에서는 true
	private static final String COOKIE_PATH = "/";
	private static final int ACCESS_TOKEN_MAX_AGE = 60 * 60; // 1시간
	private static final int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 7; // 7일
	private final JwtService jwtService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		log.info("OAuth2 Login 성공");
		try {
			CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
			// 사용자 Role 확인
			Role userRole = oAuth2User.getRole();
			//토큰 생성
			String accessToken = jwtService.createAccessToken(oAuth2User.getEmail(), oAuth2User.getRole().name());
			String refreshToken = null;
			if (userRole == Role.USER) {
				refreshToken = jwtService.createRefreshToken();
				jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
			}
			logTokens(accessToken, refreshToken);
			setTokensInCookie(response, accessToken, refreshToken);
			response.sendRedirect("http://localhost:3000/oauth2/callback");
		} catch (Exception e) {
			log.error("OAuth2 로그인 처리 중 오류 발생: {} ", e.getMessage());
			throw e;
		}
	}

	// 쿠키 설정 메소드 생성
	private void setTokensInCookie(HttpServletResponse response, String accessToken, String refreshToken) {

		// Access Token 쿠키 설정
		Cookie accessTokenCookie = new Cookie("AccessToken", accessToken);
		accessTokenCookie.setHttpOnly(COOKIE_HTTP_ONLY);
		accessTokenCookie.setSecure(COOKIE_SECURE);
		accessTokenCookie.setPath(COOKIE_PATH);
		accessTokenCookie.setMaxAge(ACCESS_TOKEN_MAX_AGE);

		// Refresh Token 쿠키 설정 (필요 시)
		Cookie refreshTokenCookie = null;
		if (refreshToken != null) {
			refreshTokenCookie = new Cookie("RefreshToken", refreshToken);
			refreshTokenCookie.setHttpOnly(COOKIE_HTTP_ONLY);
			refreshTokenCookie.setSecure(COOKIE_SECURE);
			refreshTokenCookie.setPath(COOKIE_PATH);
			refreshTokenCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
		}

		// SameSite 설정
		response.addHeader("Set-Cookie", "AccessToken=" + accessToken +
			"; HttpOnly; Secure=" + COOKIE_SECURE + "; SameSite=None; Path=" + COOKIE_PATH + "; Max-Age="
			+ ACCESS_TOKEN_MAX_AGE);

		if (refreshToken != null) {
			response.addHeader("Set-Cookie", "RefreshToken=" + refreshToken +
				"; HttpOnly; Secure=" + COOKIE_SECURE + "; SameSite=None; Path=" + COOKIE_PATH + "; Max-Age="
				+ REFRESH_TOKEN_MAX_AGE);
		}
		// 응답에 쿠키 추가
		response.addCookie(accessTokenCookie);
		if (refreshTokenCookie != null) {
			response.addCookie(refreshTokenCookie);
		}

	}

	// 로그용 (삭제해도 ok)
	private void logTokens(String accessToken, String refreshToken) {
		log.info("AccessToken: {}", accessToken);
		if (refreshToken != null) {
			log.info("RefreshToken: {}", refreshToken);
		}
	}
}

