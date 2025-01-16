package com.example.eatmate.app.domain.chatRoom.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.eatmate.app.domain.chat.dto.response.ChatMessageResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomResponseDto {
	private Page<ChatMessageResponseDto> chats;
	private List<ChatMemberDto> participants;

	@Builder
	private ChatRoomResponseDto(Page<ChatMessageResponseDto> chats, List<ChatMemberDto> participants) {
		this.chats = chats;
		this.participants = participants;
	}

	public static ChatRoomResponseDto of(List<ChatMemberDto> participants, Page<ChatMessageResponseDto> chatPage) {
		return ChatRoomResponseDto.builder()
			.chats(chatPage)
			.participants(participants)
			.build();
	}
}
