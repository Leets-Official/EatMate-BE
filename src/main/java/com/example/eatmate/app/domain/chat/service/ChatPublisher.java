package com.example.eatmate.app.domain.chat.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.chat.dto.ChatDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatPublisher {

	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq.exchange}")
	private String exchange;

	public void sendMessage(Long chatRoomId, ChatDto chatDto) {
		String bindingKey = "chat.room." + chatRoomId;
		rabbitTemplate.convertAndSend(exchange, bindingKey, chatDto);
	}
}
