package com.example.eatmate.app.domain.member.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Role {
	USER("사용자"),
	ADMIN("관리자"),
	GUEST("게스트"); //첫 로그인 구별

	private final String RoleType;

	Role(String roleType) {
		this.RoleType = roleType;
	}

}
