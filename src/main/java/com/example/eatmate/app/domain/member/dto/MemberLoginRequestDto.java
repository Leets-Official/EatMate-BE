package com.example.eatmate.app.domain.member.dto;

import com.example.eatmate.app.domain.member.domain.Gender;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberLoginRequestDto {
	private String email;
	private String name;
	private String nickname;
	private Gender gender;
}
