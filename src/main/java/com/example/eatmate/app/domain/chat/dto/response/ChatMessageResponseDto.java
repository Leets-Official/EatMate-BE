package com.example.eatmate.app.domain.chat.dto.response;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.chat.domain.Chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageResponseDto {
	Long chatId;
	Long senderId;
	Long chatRoomId;
	String content;
	LocalDateTime regDate;

	@Builder
	private ChatMessageResponseDto(Long chatId, Long senderId, Long chatRoomId, String content, LocalDateTime regDate) {
		this.chatId = chatId;
		this.chatRoomId = chatRoomId;
		this.senderId = senderId;
		this.content = content;
		this.regDate = regDate;
	}

	public static ChatMessageResponseDto from(Chat chat) {
		return ChatMessageResponseDto.builder()
			.chatId(chat.getId())
			.senderId(chat.getSender().getMemberId())
			.chatRoomId(chat.getChatRoom().getId())
			.content(chat.getContent())
			.regDate(chat.getCreatedAt())
			.build();
	}

	public static ChatMessageResponseDto of(Long memberId, Long chatRoomId, String content, LocalDateTime regDate) {
		return ChatMessageResponseDto.builder()
			.chatId(null)
			.chatRoomId(chatRoomId)
			.senderId(memberId)
			.content(content)
			.regDate(regDate)
			.build();
	}
}
