package com.example.eatmate.app.domain.chatRoom.dto.response;

import com.example.eatmate.app.domain.member.domain.Mbti;
import com.example.eatmate.app.domain.member.domain.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMemberResponseDto {
	private String memberName;
	private String nickname;
	private Mbti mbti;

	@Builder
	private ChatMemberResponseDto(String memberName, String nickname, Mbti mbti) {
		this.memberName = memberName;
		this.nickname = nickname;
		this.mbti = mbti;
	}

	public static ChatMemberResponseDto from(Member member) {
		return ChatMemberResponseDto.builder()
			.memberName(member.getName())
			.nickname(member.getNickname())
			.mbti(member.getMbti())
			.build();
	}
}
