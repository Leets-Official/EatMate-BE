package com.example.eatmate.app.domain.member.dto;

import com.example.eatmate.app.domain.member.domain.Mbti;
import com.example.eatmate.app.domain.member.domain.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoResponseDto {
	private Long memberId;        // memberId 추가
	private String profileImageUrl;
	private String nickname;
	private Mbti mbti;

	@Builder
	public UserInfoResponseDto(Long memberId, String profileImageUrl, String nickname, Mbti mbti) {
		this.memberId = memberId;
		this.profileImageUrl = profileImageUrl;
		this.nickname = nickname;
		this.mbti = mbti;
	}

	// 정적 팩토리 메서드
	public static UserInfoResponseDto from(Member member) {
		return UserInfoResponseDto.builder()
			.memberId(member.getMemberId())
			.profileImageUrl(
				member.getProfileImage() != null ? member.getProfileImage().getImageUrl() : null
			)
			.nickname(member.getNickname())
			.mbti(member.getMbti())
			.build();
	}
}
