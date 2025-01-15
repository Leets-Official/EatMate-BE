package com.example.eatmate.app.domain.chatRoom.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.chat.service.ChatPublisher;
import com.example.eatmate.app.domain.chatRoom.dto.response.ChatRoomResponseDto;
import com.example.eatmate.app.domain.chatRoom.service.ChatRoomService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatPublisher chatPublisher;
	private final ChatRoomService chatRoomService;

	@GetMapping("/{chatRoomId}")
	@Operation(summary = "채팅방 입장", description = "채팅방에 입장합니다.")
	public ResponseEntity<GlobalResponseDto<ChatRoomResponseDto>> enterChatRoom(
		@PathVariable Long chatRoomId,
		@AuthenticationPrincipal UserDetails userDetails,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(chatRoomService.enterChatRoom(chatRoomId, userDetails, pageable)));
	}

	@PatchMapping("/{chatRoomId}")
	@Operation(summary = "채팅방 나가기", description = "채팅방을 나갑니다.")
	public ResponseEntity<GlobalResponseDto<String>> quitChatRoom(
		@PathVariable Long chatRoomId,
		@AuthenticationPrincipal UserDetails userDetails) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(chatRoomService.leaveChatRoom(chatRoomId, userDetails)));
	}
	//알림 관련
	/*@MessageMapping("chat.enter.{chatRoomId}")
	@Operation(summary = "채팅방 입장", description = "채팅에 입장합니다.")
	public void enter(ChatDto chatDto, @DestinationVariable Long chatRoomId) {
		chatDto.of(chatDto.chatId(), chatDto.senderId(), chatRoomId, "입장하셨습니다.", chatDto.regDate());
		chatPublisher.sendMessage(chatDto.chatRoomId(), chatDto);
	}

	@MessageMapping("chat.leave.{chatRoomId}")
	@Operation(summary = "채팅방 퇴장", description = "채팅에 입장합니다.")
	public void leave(ChatDto chatDto, @DestinationVariable Long chatRoomId) {
		chatDto.of(chatDto.chatId(), chatDto.senderId(), chatRoomId, "퇴장하셨습니다.", chatDto.regDate());
		chatPublisher.sendMessage(chatDto.chatRoomId(), chatDto);
	}*/
}
