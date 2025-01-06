package com.example.eatmate.app.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberLoginResponseDto {

	private String accessToken;
	private String refreshToken;

	@Builder
	public MemberLoginResponseDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public static MemberLoginResponseDto of(String accessToken, String refreshToken) {
		return MemberLoginResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}

