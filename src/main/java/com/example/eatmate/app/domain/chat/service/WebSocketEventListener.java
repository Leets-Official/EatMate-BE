package com.example.eatmate.app.domain.chat.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketEventListener {

	private final SimpMessagingTemplate messagingTemplate;

	public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@EventListener
	public void handleSubscribeEvent(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String destination = headerAccessor.getDestination();

		if (destination != null && destination.startsWith("/topic/chat.")) {
			log.info("WebSocket 구독 성공: {}", destination);

			Long chatRoomId = extractChatRoomId(destination);

			if (chatRoomId != null) {
				Map<String, Object> response = new HashMap<>();
				response.put("roomId", chatRoomId);
				response.put("messageType", "SUBSCRIBE_SUCCESS");
				response.put("timestamp", LocalDateTime.now());

				messagingTemplate.convertAndSend(destination, response);
			}
		}
	}

	private Long extractChatRoomId(String destination) {
		try {
			return Long.parseLong(destination.substring(destination.lastIndexOf(".") + 1));
		} catch (NumberFormatException e) {
			log.error("채팅방 ID 파싱 실패: {}", destination);
			return null;
		}
	}
}
