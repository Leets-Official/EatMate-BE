package com.example.eatmate.app.domain.chatRoom.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMemberListDto {

	private Long memberId;
	private String nickname;

	@Builder
	private ChatMemberListDto(Long memberId, String nickname) {
		this.memberId = memberId;
		this.nickname = nickname;
	}

	public static ChatMemberListDto of(Long memberId, String nickname) {
		return ChatMemberListDto.builder()
			.memberId(memberId)
			.nickname(nickname)
			.build();
	}
}
