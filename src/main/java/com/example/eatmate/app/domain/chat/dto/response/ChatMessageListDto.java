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
	private ChatMessageListDto(List<ChatMessageResponseDto> chats, int pageNumber, boolean isLast) {
		this.chats = chats;
		this.pageNumber = pageNumber;
		this.isLast = isLast;
	}

	public static ChatMessageListDto from(Slice<ChatMessageResponseDto> chatList) {
		return ChatMessageListDto.builder()
			.chats(chatList.getContent())
			.pageNumber(chatList.getNumber())
			.isLast(chatList.isLast())
			.build();
	}
}
