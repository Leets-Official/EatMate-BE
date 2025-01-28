package com.example.eatmate.global.auth.jwt;

import java.util.List;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtService jwtService;

	@Override
	public boolean beforeHandshake(
		ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		// JWT 토큰 추출 (Authorization 헤더 사용)
		String jwtToken = extractJwtFromRequest(request);

		if (jwtToken != null && jwtService.isTokenValid(jwtToken)) {
			// 인증 정보 SecurityContext 설정
			//Authentication authentication = jwtService.getAuthentication(jwtToken);
			//SecurityContextHolder.getContext().setAuthentication(authentication);
			return true;
		}
		// 인증 실패 시 연결 차단
		return false;
	}

	@Override
	public void afterHandshake(
		ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Exception exception) {
	}

	private String extractJwtFromRequest(ServerHttpRequest request) {
		List<String> authHeaders = request.getHeaders().getOrEmpty("Authorization");
		if (!authHeaders.isEmpty() && authHeaders.get(0).startsWith("Bearer ")) {
			return authHeaders.get(0).substring(7);
		}
		return null;
	}
}
//알림 관련 컨트롤러(채팅방)
	/*@MessageMapping("chat.enter.{chatRoomId}")
	@Operation(summary = "채팅방 입장", description = "채팅에 입장합니다.")
	public void enter(ChatDto chatDto, @DestinationVariable Long chatRoomId) {
		chatDto.of(chatDto.chatId(), chatDto.senderId(), chatRoomId, "입장하셨습니다.", chatDto.regDate());
		chatPublisher.sendMessage(chatDto.chatRoomId(), chatDto);
	}

	@MessageMapping("chat.leave.{chatRoomId}")
	@Operation(summary = "채팅방 퇴장", description = "채팅에 입장합니다.")
	public void leave(ChatDto chatDto, @DestinationVariable Long chatRoomId) {
		chatDto.of(chatDto.chatId(), chatDto.senderId(), chatRoomId, "퇴장하셨습니다.", chatDto.regDate());
		chatPublisher.sendMessage(chatDto.chatRoomId(), chatDto);
	}*/