package com.example.eatmate.global.auth.jwt;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest) {
			HttpServletRequest servletRequest = ((ServletServerHttpRequest)request).getServletRequest();

			log.info("WebSocket 요청 감지: {}", servletRequest.getRequestURI());

			//요청된 쿠키 확인
			if (servletRequest.getCookies() != null) {
				for (Cookie cookie : servletRequest.getCookies()) {
					log.info("쿠키 확인: {} = {}", cookie.getName(), cookie.getValue());
				}
			} else {
				log.warn("WebSocket 요청에 쿠키 없음!");
			}

			//쿠키에서 AccessToken 추출
			Optional<String> tokenOptional = jwtService.extractAccessTokenFromCookie(servletRequest);

			tokenOptional
				.filter(jwtService::isTokenValid)
				.flatMap(jwtService::extractEmail)
				.flatMap(memberRepository::findByEmail)
				.ifPresentOrElse(member -> {
					log.info("WebSocket 인증 성공! 사용자: {}", member.getEmail());
					attributes.put("userDetails", member); //WebSocket 세션에 사용자 정보 저장
				}, () -> {
					log.warn("WebSocket 인증 실패: JWT 없음 또는 유효하지 않음");
				});
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Exception exception) {
	}
}
