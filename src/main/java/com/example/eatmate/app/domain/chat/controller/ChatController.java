package com.example.eatmate.app.domain.chat.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.chat.dto.request.ChatMessageRequestDto;
import com.example.eatmate.app.domain.chat.service.ChatService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@MessageMapping("chat.{chatRoomId}")
	@Operation(summary = "채팅 메세지 전송", description = "채팅을 메세지를 전송합니다.")
	public void sendChatMessage(
		@DestinationVariable Long chatRoomId,
		@Valid ChatMessageRequestDto chatMessageDto) {

		log.info("sender: sendChat: {}", chatMessageDto);
		//채팅 전송(메세지 발행)
		chatService.sendChatMessage(chatMessageDto);
	}

	//보완적 함수 웹소켓 끊어졌을 경우
	@PostMapping("/api/chat/{chatRoomId}")
	@Operation(summary = "채팅 메세지 전송 대체 수단", description = "채팅을 메세지를 대체 방안을 통해 전송합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> sendChatMessageAlter(
		@PathVariable Long chatRoomId,
		@RequestBody @Valid ChatMessageRequestDto chatMessageDto) {

		chatService.sendChatMessage(chatMessageDto);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GlobalResponseDto.success());
	}
}
