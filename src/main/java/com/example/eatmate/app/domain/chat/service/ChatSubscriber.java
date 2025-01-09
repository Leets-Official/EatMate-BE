package com.example.eatmate.app.domain.chat.service;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.eatmate.app.domain.chat.dto.ChatDto;
import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSubscriber {

	private final SimpMessagingTemplate messagingTemplate;

	//@RabbitListener(queues = "chat.queue.{chatRoomId}", ackMode = "MANUAL")
	@RabbitListener(queues = "#{queueNameResolver.resolveQueueName(#chatRoomId)}", ackMode = "MANUAL")
	public void receiveMessage(ChatDto chatDto, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
		try {
			log.info("Received chat message: {}", chatDto.content());
			messagingTemplate.convertAndSend("/topic/chatRoom/" + chatDto.chatRoomId(), chatDto);
			// 처리 성공 시 ACK
			channel.basicAck(deliveryTag, false);
		} catch (Exception e) {
			log.error("Error processing message: {}", chatDto, e);
			try {
				// 처리 실패 시 NACK → DLQ로 이동
				channel.basicNack(deliveryTag, false, false);
			} catch (IOException nackException) {
				log.error("Failed to send NACK for message: {}", chatDto, nackException);
			}
		}
	}

	@RabbitListener(queues = "dead.letter.queue")
	public void processDeadLetterQueue(ChatDto chatDto) {
		log.error("Dead Letter Queue received message: {}", chatDto);

		try {
			// 메시지 복구 로직
			messagingTemplate.convertAndSend("/topic/chatRoom/" + chatDto.chatRoomId(), chatDto);
		} catch (Exception e) {
			log.error("Failed to process dead letter message: {}", chatDto, e);
		}
		}
}
