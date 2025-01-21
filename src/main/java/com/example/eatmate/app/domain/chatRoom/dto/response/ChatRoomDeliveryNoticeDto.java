package com.example.eatmate.app.domain.chatRoom.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomDeliveryNoticeDto {
	private String store;
	private String account;
	private String bank;
	private String pickup;

	@Builder
	private ChatRoomDeliveryNoticeDto(String store, String account, String bank, String pickup) {
		this.store = store;
		this.account = account;
		this.pickup = pickup;
		this.bank = bank;
	}

	public static ChatRoomDeliveryNoticeDto of(String store, String account, String bank, String pickup) {
		return ChatRoomDeliveryNoticeDto.builder()
			.store(store)
			.account(account)
			.pickup(pickup)
			.bank(bank)
			.build();
	}
}
