package com.example.eatmate.app.domain.chatRoom.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomOfflineNoticeDto {
	private String store;
	private LocalDateTime time;

	@Builder
	private ChatRoomOfflineNoticeDto(String store, LocalDateTime time) {
		this.store = store;
		this.time = time;
	}

	public static ChatRoomOfflineNoticeDto of(String store, LocalDateTime time) {
		return ChatRoomOfflineNoticeDto.builder()
			.store(store)
			.time(time)
			.build();
	}
}
