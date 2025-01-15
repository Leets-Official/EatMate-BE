package com.example.eatmate.app.domain.chat.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.chat.domain.Chat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public record ChatMessageDto(
	Long chatId,
	Long senderId,
	Long chatRoomId,
	String content,
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	LocalDateTime regDate
){
	private ChatMessageDto(Chat chat) {
		this(chat.getId(), chat.getSender().getMemberId(), chat.getChatRoom().getId(), chat.getContent(),chat.getCreatedAt());
	}

	public static ChatMessageDto from(Chat chat) {
		return new ChatMessageDto(chat.getId(), chat.getSender().getMemberId(), chat.getChatRoom().getId(), chat.getContent(), chat.getCreatedAt());
	}

	public static ChatMessageDto of(Long chatId, Long senderId, Long chatRoomId, String content, LocalDateTime regDate) {
		return new ChatMessageDto(chatId, senderId, chatRoomId, content,regDate);
	}
}
