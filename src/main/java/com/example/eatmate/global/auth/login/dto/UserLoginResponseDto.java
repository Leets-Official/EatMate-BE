package com.example.eatmate.global.auth.login.dto;

import com.example.eatmate.app.domain.member.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponseDto {

	private String email;
	private Role role;
}
