package com.example.eatmate.app.domain.chat.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageListDto {

	private List<ChatMessageResponseDto> chats;
	private int pageNumber;
	private boolean isLast;

	@Builder
	private ChatMessageListDto(Slice<ChatMessageResponseDto> chatList) {
		this.chats = chatList.getContent();
		this.pageNumber = chatList.getNumber();
		this.isLast = chatList.isLast();
	}

	public static ChatMessageListDto from(Slice<ChatMessageResponseDto> chatList) {
		return ChatMessageListDto.builder()
			.chatList(chatList)
			.build();
	}
}
