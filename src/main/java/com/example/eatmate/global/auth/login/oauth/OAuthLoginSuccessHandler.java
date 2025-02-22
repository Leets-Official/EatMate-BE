package com.example.eatmate.global.auth.login.oauth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.eatmate.app.domain.member.domain.Gender;
import com.example.eatmate.app.domain.member.domain.Role;
import com.example.eatmate.global.auth.jwt.JwtService;

import jakarta.servlet.ServletException;
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
			Gender userGender = oAuth2User.getGender();
			//토큰 생성
			String accessToken = jwtService.createAccessToken(oAuth2User.getEmail(), oAuth2User.getRole().name(),
				userRole == Role.USER ? userGender.name() : null);
			String refreshToken = null;
			if (userRole == Role.USER) {
				refreshToken = jwtService.createRefreshToken();
				jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
			}
			logTokens(accessToken, refreshToken);
			setTokensInCookie(response, accessToken, refreshToken);
			response.sendRedirect("https://develop.d4u0qurydeei4.amplifyapp.com/intro/oauth2/callback");
		} catch (Exception e) {
			log.error("OAuth2 로그인 처리 중 오류 발생: {} ", e.getMessage());
			throw e;
		}
	}

	// 쿠키 설정 메소드 생성
	private void setTokensInCookie(HttpServletResponse response, String accessToken, String refreshToken) {
		// Access Token 쿠키 설정
		String accessTokenCookieString = String.format("AccessToken=%s; " +
				"Path=%s; " +
				"Max-Age=%d; " +
				"HttpOnly; " +
				"Secure; " +
				"SameSite=None",
			accessToken,
			COOKIE_PATH,
			ACCESS_TOKEN_MAX_AGE);

		response.addHeader("Set-Cookie", accessTokenCookieString);

		// Refresh Token 쿠키 설정
		if (refreshToken != null) {
			String refreshTokenCookieString = String.format("RefreshToken=%s; " +
					"Path=%s; " +
					"Max-Age=%d; " +
					"HttpOnly; " +
					"Secure; " +
					"SameSite=None",
				refreshToken,
				COOKIE_PATH,
				REFRESH_TOKEN_MAX_AGE);

			response.addHeader("Set-Cookie", refreshTokenCookieString);
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

