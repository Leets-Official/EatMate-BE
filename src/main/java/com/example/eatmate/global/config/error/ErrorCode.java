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

	// 성별 필수 선택
	INVALID_GENDER(400, "INVALID_GENDER", "성별 선택은 필수입니다."),

	// 유효하지 않은 전화번호
	INVALID_PHONE_NUMBER(400, "INVALID_PHONE_NUMBER", "유효하지 않은 전화번호입니다."),

	// 유효하지 않은 학번
	INVALID_STUDENT_NUMBER(400, "INVALID_STUDENT_NUMBER", "유효하지 않은 학번입니다."),

	//유효하지 않은 요청
	VALIDATION_FAILED(400, "VALIDATION_FAILED", "요청 값이 올바르지 않습니다."),

	//회원
	USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),

	//중복된 회원
	MEMBER_ALREADY_EXISTS(409, "MEMBER_ALREADY_EXISTS", "이미 가입된 이메일입니다."),

	//중복된 전화번호
	DUPLICATE_PHONE_NUMBER(409, "DUPLICATE_PHONE_NUMBER", "이미 사용 중인 전화번호입니다."),

	//중복된 학번
	DUPLICATE_STUDENT_NUMBER(409, "DUPLICATE_STUDENT_NUMBER", "이미 사용 중인 학번입니다."),

	//중복 닉네임
	DUPLICATE_NICKNAME(409, "NICKNAME_ALREADY_EXISTS", "이미 존재하는 닉네임입니다.");

	private final int status;
	private final String code;
	private final String message;
}
