package com.example.eatmate.global.config.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//공통
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 에러"),

	//유효하지 않은 토큰
	INVALID_TOKEN(401, "INVALID_TOKEN",  "유효하지 않은 토큰입니다."),

	//유효하지 않은 Mbti
	INVALID_MBTI(404, "INVALID_MBTI" , "유효하지 않은 MBTI입니다."),

	//회원
	USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),

	//중복된 회원
	MEMBER_ALREADY_EXISTS(409, "MEMBER_ALREADY_EXISTS", "이미 가입된 이메일입니다.");


	private final int status;
	private final String code;
	private final String message;
}
