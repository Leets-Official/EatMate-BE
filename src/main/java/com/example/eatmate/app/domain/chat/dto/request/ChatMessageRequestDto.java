package com.example.eatmate.app.domain.chat.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequestDto {
	@NotNull
	Long senderId;
	@NotNull
	Long chatRoomId;
	@NotNull
	String content;
	@NotNull
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	LocalDateTime regDate;
}
