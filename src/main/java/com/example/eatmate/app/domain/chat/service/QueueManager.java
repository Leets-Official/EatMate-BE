package com.example.eatmate.app.domain.chat.service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueManager {

	@Value("${rabbitmq.queue-prefix}")
	private String queuePrefix;

	@Value("${rabbitmq.binding-key-prefix}")
	private String bindingKeyPrefix;

	@Value("${rabbitmq.exchange}")
	private String exchangeName;

	private final AmqpAdmin amqpAdmin;
	private final SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory;
	private final SimpMessagingTemplate messagingTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Map<Long, SimpleMessageListenerContainer> activeListeners = new ConcurrentHashMap<>();

	//큐 생성(채팅방 생성과 동시에)
	public void createQueueForChatRoom(Long chatRoomId) {

		String queueName = queuePrefix + chatRoomId;
		String bindingKey = bindingKeyPrefix + chatRoomId;

		// DLQ 설정 추가
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", "dlx.exchange"); // Dead Letter Exchange 설정
		args.put("x-dead-letter-routing-key", "dead.letter.queue"); // Dead Letter Queue 라우팅 키
		args.put("x-message-ttl", 60000); //60초

		Queue chatQueue = new Queue(queueName, true, false, false, args);
		amqpAdmin.declareQueue(chatQueue);

		Binding binding = BindingBuilder.bind(chatQueue)
			.to(new TopicExchange(exchangeName))
			.with(bindingKey);
		amqpAdmin.declareBinding(binding);

		startChatRoomListener(chatRoomId);
	}

	//채팅방 나가기(모임 종료 시) 큐 할당 해제
	public void deleteQueueForChatRoom(Long chatRoomId) {
		String queueName = queuePrefix + chatRoomId;
		amqpAdmin.deleteQueue(queueName);
	}

	// 리스너 시작
	public void startChatRoomListener(Long chatRoomId) {
		if (activeListeners.containsKey(chatRoomId)) {
			log.warn("Listener for chat room {} is already running", chatRoomId);
			return;
		}
		String queueName = queuePrefix + chatRoomId;

		SimpleMessageListenerContainer container = rabbitListenerContainerFactory.createListenerContainer();
		container.setQueueNames(queueName);
		container.setMessageListener(message -> {
			try {
				String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
				ChatMessageDto chatMessage = objectMapper.readValue(messageBody, ChatMessageDto.class);

				// WebSocket 메시지 전달
				log.info("Sending message to chatRoom {}: {}", chatRoomId, chatMessage);
				messagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId, chatMessage);
			} catch (Exception e) {
				log.error("Failed to process message for chat room {}: {}", chatRoomId, e.getMessage());
			}
		});

		container.start();
		activeListeners.put(chatRoomId, container);
	}

	public void stopChatRoomListener(Long chatRoomId) {
		SimpleMessageListenerContainer container = activeListeners.remove(chatRoomId);
		if (container != null) {
			container.stop();
			log.info("Stopped listener for chat room {}", chatRoomId);
		} else {
			log.warn("No active listener found for chat room {}", chatRoomId);
		}
	}

	@PreDestroy
	public void cleanup() {
		activeListeners.values().forEach(SimpleMessageListenerContainer::stop);
		activeListeners.clear();
		log.info("All listeners have been stopped and cleaned up.");
	}

	@RabbitListener(queues = "dead.letter.queue", containerFactory = "rabbitListenerContainerFactory")
	public void processDeadLetterQueue(Message message) {
		try {
			// 메시지 복구 로직
			String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
			ChatMessageDto chatDto = objectMapper.readValue(messageBody, ChatMessageDto.class);
			log.error("Dead Letter Queue received message: {}", chatDto);
			messagingTemplate.convertAndSend("/topic/chatRoom/" + chatDto.chatRoomId(), chatDto);
		} catch (Exception e) {
			log.error("Failed to process dead letter message: {}", e.getMessage(), e);
		}
	}
}
