package com.example.eatmate.app.domain.chat.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.chat.dto.ChatDto;
import com.example.eatmate.app.domain.chat.service.ChatPublisher;
import com.example.eatmate.app.domain.chat.service.ChatService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChatController {

	private final ChatPublisher chatPublisher;
	private final ChatService chatService;

	@MessageMapping("/chat")
	@Operation(summary = "채팅 전송", description = "채팅을 전송합니다.")
	public void sendChat(ChatDto chatDto) {
		//채팅 전송(메세지 발행)
		chatPublisher.sendMessage(chatDto.chatRoomId(), chatDto);
		chatService.saveChat(chatDto);
	}

	//보완적 함수 웹소켓 끊어졌을 경우
	@PostMapping("/chat")
	@Operation(summary = "채팅 전송", description = "채팅을 전송합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> receiveChat(@RequestBody ChatDto chatDto) {
		chatPublisher.sendMessage(chatDto.chatRoomId(), chatDto);
		chatService.saveChat(chatDto);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success());
	}
}
