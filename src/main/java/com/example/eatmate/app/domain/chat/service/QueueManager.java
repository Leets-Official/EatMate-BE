package com.example.eatmate.app.domain.chat.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.chat.dto.request.ChatMessageRequestDto;
import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.DeletedStatus;
import com.example.eatmate.app.domain.chatRoom.domain.repository.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueManager {

	private final AmqpAdmin amqpAdmin;
	private final SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory;
	private final SimpMessagingTemplate messagingTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Map<Long, SimpleMessageListenerContainer> activeListeners = new ConcurrentHashMap<>();
	private final ChatRoomRepository chatRoomRepository;
	@Value("${rabbitmq.queue-prefix}")
	private String queuePrefix;
	@Value("${rabbitmq.binding-key-prefix}")
	private String bindingKeyPrefix;
	@Value("${rabbitmq.exchange}")
	private String exchangeName;

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
		container.setMessageListener((ChannelAwareMessageListener)(message, channel) -> {
			try {
				String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
				ChatMessageRequestDto chatMessage = objectMapper.readValue(messageBody, ChatMessageRequestDto.class);

				// WebSocket 메시지 전달
				log.info("Sending message to chatRoom {}: {}", chatRoomId, chatMessage);
				messagingTemplate.convertAndSend("/topic/chat." + chatMessage.getChatRoomId(), chatMessage);
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			} catch (Exception e) {
				log.error("Failed to process message for chat room {}: {}", chatRoomId, e.getMessage());
				channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
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

	//서버 다운 이후 복구했을 경우 채팅방의 컨슈머 복구
	@Bean
	public ApplicationRunner restoreChatRoomListenersRunner() {
		return args -> {
			List<ChatRoom> activeChatRoom = chatRoomRepository.findAllByDeletedStatus(DeletedStatus.NOT_DELETED);
			for (ChatRoom chatRoom : activeChatRoom) {
				startChatRoomListener(chatRoom.getId());
			}
			log.info("✅ Restored {} chat room listeners from DB", activeChatRoom.size());
		};
	}

	@PreDestroy
	public void cleanup() {
		activeListeners.values().forEach(SimpleMessageListenerContainer::stop);
		activeListeners.clear();
		log.info("All listeners have been stopped and cleaned up.");
	}

	@RabbitListener(queues = "dead.letter.queue", containerFactory = "rabbitListenerContainerFactory")
	public void processDeadLetterQueue(Message message, Channel channel) {
		try {
			// 메시지 복구 로직
			String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
			ChatMessageRequestDto chatMessage = objectMapper.readValue(messageBody, ChatMessageRequestDto.class);
			log.error("Dead Letter Queue received message: {}", chatMessage);
			messagingTemplate.convertAndSend("/topic/chat." + chatMessage.getChatRoomId(), chatMessage);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			try {
				// 처리 실패 시 NACK로 메시지 재처리 방지 -> 필요시 true로 변경하면 재처리 가능
				channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
			} catch (IOException ex) {
				log.error("Failed to nack message: {}", ex.getMessage(), ex);
			}
		}
	}
}
