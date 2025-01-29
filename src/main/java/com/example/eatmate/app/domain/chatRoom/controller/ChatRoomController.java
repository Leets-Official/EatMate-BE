package com.example.eatmate.app.domain.chatRoom.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.chat.dto.response.ChatMessageListDto;
import com.example.eatmate.app.domain.chat.service.ChatService;
import com.example.eatmate.app.domain.chatRoom.dto.response.ChatRoomResponseDto;
import com.example.eatmate.app.domain.chatRoom.service.ChatRoomService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatRoomService chatRoomService;
	private final ChatService chatService;

	@GetMapping("/{chatRoomId}")
	@Operation(summary = "채팅방 입장", description = "채팅방에 입장합니다.")
	public ResponseEntity<GlobalResponseDto<ChatRoomResponseDto>> enterChatRoom(
		@PathVariable Long chatRoomId,
		@AuthenticationPrincipal UserDetails userDetails,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(chatRoomService.enterChatRoomAndLoadMessage(chatRoomId, userDetails, pageable)));
	}

	@GetMapping("/{chatRoomId}/past")
	@Operation(summary = "채팅방의 과거 채팅 로딩", description = "채팅방의 과거 채팅을 로딩합니다.")
	public ResponseEntity<GlobalResponseDto<ChatMessageListDto>> loadChatRoomPastMessage(
		@PathVariable Long chatRoomId,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(chatService.convertChatList(chatRoomId, cursor, pageable)));
	}

	@PatchMapping("/{chatRoomId}")
	@Operation(summary = "채팅방 나가기", description = "채팅방을 나갑니다.")
	public ResponseEntity<GlobalResponseDto<Void>> leftChatRoom(
		@PathVariable Long chatRoomId,
		@AuthenticationPrincipal UserDetails userDetails) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(chatRoomService.leaveChatRoom(chatRoomId, userDetails)));
	}
}
