package com.example.eatmate.global.auth.login.oauth;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuthLoginFailureHandler implements AuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {
		// 원인 예외 찾기
		Throwable cause = exception.getCause();
		while (cause != null && !(cause instanceof CommonException)) {
			cause = cause.getCause();
		}

		// CommonException이고 이메일 도메인 에러인 경우
		if (cause instanceof CommonException) {
			CommonException commonException = (CommonException)cause;
			if (commonException.getErrorCode() == ErrorCode.INVALID_EMAIL_DOMAIN) {
				log.error("가천대 이메일이 아님: {}", exception.getMessage());
				response.sendRedirect("/invalid-email.html");
				return;
			}
		}

		// 그 외의 소셜 로그인 실패
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("Error-Message", "소셜 로그인 실패");
		response.setHeader("Error-Detail", exception.getMessage());
		log.error("소셜 로그인 실패: {}", exception.getMessage());
	}
}

