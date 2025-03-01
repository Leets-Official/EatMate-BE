package com.example.eatmate.global.auth.jwt;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;

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

			// [1] Authorization 헤더에서 토큰 추출
			String authHeader = servletRequest.getHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7); // "Bearer " 제거

				// [2] 토큰 유효성 검사 및 사용자 정보 추출
				if (jwtService.isTokenValid(token)) {
					jwtService.extractEmail(token)
						.flatMap(memberRepository::findByEmail)
						.ifPresentOrElse(member -> {
							log.info("WebSocket 인증 성공! 사용자: {}", member.getEmail());
							attributes.put("userDetails", member); // WebSocket 세션에 사용자 정보 저장
						}, () -> {
							log.warn("WebSocket 인증 실패: 토큰에서 이메일 추출 실패 또는 회원 미존재");
						});
				} else {
					log.warn("WebSocket 인증 실패: 토큰 유효하지 않음");
				}
			} else {
				log.warn("WebSocket 요청에 Authorization 헤더가 없거나 Bearer 스키마가 아님");
			}

			// [기존 쿠키 기반 로직 제거 혹은 주석 처리]
			// if (servletRequest.getCookies() != null) {
			//     for (Cookie cookie : servletRequest.getCookies()) {
			//         log.info("쿠키 확인: {} = {}", cookie.getName(), cookie.getValue());
			//     }
			// } else {
			//     log.warn("WebSocket 요청에 쿠키 없음!");
			// }
			// Optional<String> tokenOptional = jwtService.extractAccessToken(servletRequest);
			// tokenOptional
			//     .filter(jwtService::isTokenValid)
			//     .flatMap(jwtService::extractEmail)
			//     .flatMap(memberRepository::findByEmail)
			//     .ifPresentOrElse(...);
		}

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Exception exception) {
	}
}
