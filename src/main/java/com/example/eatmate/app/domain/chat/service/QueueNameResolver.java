package com.example.eatmate.app.domain.chat.service;

import org.springframework.stereotype.Component;

@Component
public class QueueNameResolver {
	public String resolveQueueName(Long chatRoomId) {
		return "chat.queue." + chatRoomId;
	}
}
