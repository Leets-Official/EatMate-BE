package com.example.eatmate.global.config.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//공통
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 에러"),

	//회원
	USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다.");

	private final int status;
	private final String code;
	private final String message;
}
