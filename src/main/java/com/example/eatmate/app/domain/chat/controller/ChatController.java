package com.example.eatmate.app.domain.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.chat.dto.ChatMessageDto;
import com.example.eatmate.app.domain.chat.service.ChatPublisher;
import com.example.eatmate.app.domain.chat.service.ChatService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

	private final ChatPublisher chatPublisher;
	private final ChatService chatService;

	@MessageMapping("chat")
	@Operation(summary = "채팅 전송", description = "채팅을 전송합니다.")
	public void sendChat(ChatMessageDto chatDto) {
		log.info("sendChat: {}", chatDto);
		//채팅 전송(메세지 발행)
		chatPublisher.sendMessage(chatDto.chatRoomId(), chatDto);
		chatService.saveChat(chatDto);
	}

	//보완적 함수 웹소켓 끊어졌을 경우
	@PostMapping("/chat")
	@Operation(summary = "채팅 전송", description = "채팅을 전송합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> receiveChat(@RequestBody ChatMessageDto chatDto) {
		chatPublisher.sendMessage(chatDto.chatRoomId(), chatDto);
		chatService.saveChat(chatDto);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success());
	}
}
