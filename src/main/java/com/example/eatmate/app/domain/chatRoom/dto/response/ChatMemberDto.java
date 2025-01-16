package com.example.eatmate.app.domain.chatRoom.dto.response;

import com.example.eatmate.app.domain.member.domain.Mbti;
import com.example.eatmate.app.domain.member.domain.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMemberDto {
	private String memberName;
	private String nickname;
	private Mbti mbti;

	@Builder
	private ChatMemberDto(String memberName, String nickname, Mbti mbti) {
		this.memberName = memberName;
		this.nickname = nickname;
		this.mbti = mbti;
	}

	public static ChatMemberDto from(Member member) {
		return ChatMemberDto.builder()
			.memberName(member.getName())
			.nickname(member.getNickname())
			.mbti(member.getMbti())
			.build();
	}
}
