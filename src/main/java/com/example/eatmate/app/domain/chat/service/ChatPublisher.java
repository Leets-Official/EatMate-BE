package com.example.eatmate.app.domain.chat.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.chat.dto.request.ChatMessageRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatPublisher {

	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq.exchange}")
	private String exchange;

	@Value("${rabbitmq.binding-key-prefix}")
	private String bindingKeyPrefix;

	public void sendMessage(Long chatRoomId, ChatMessageRequestDto chatDto) {
		String bindingKey = bindingKeyPrefix + chatRoomId;
		rabbitTemplate.convertAndSend(exchange, bindingKey, chatDto);
	}
}
