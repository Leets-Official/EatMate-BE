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
	private List<ChatMemberResponseDto> participants;

	@Builder
	private ChatRoomResponseDto(Page<ChatMessageResponseDto> chats, List<ChatMemberResponseDto> participants) {
		this.chats = chats;
		this.participants = participants;
	}

	public static ChatRoomResponseDto of(List<ChatMemberResponseDto> participants, Page<ChatMessageResponseDto> chatPage) {
		return ChatRoomResponseDto.builder()
			.chats(chatPage)
			.participants(participants)
			.build();
	}
}
