package com.example.eatmate.app.domain.chatRoom.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.example.eatmate.app.domain.chat.dto.ChatMessageDto;
import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomResponseDto {
	private Page<ChatMessageDto> chats;
	private List<ChatMemberDto> participants;

	@Builder
	private ChatRoomResponseDto(Page<ChatMessageDto> chats, List<ChatMemberDto> participants) {
		this.chats = chats;
		this.participants = participants;
	}

	public static ChatRoomResponseDto from(ChatRoom chatRoom, Page<ChatMessageDto> chatPage) {
		List<ChatMemberDto> participants = chatRoom.getParticipant() != null ? chatRoom.getParticipant()
			.stream()
			.map(memberChatRoom -> ChatMemberDto.from(memberChatRoom.getMember()))
			.collect(Collectors.toList()) : null;

		return ChatRoomResponseDto.builder()
			.chats(chatPage)
			.participants(participants)
			.build();
	}
}
