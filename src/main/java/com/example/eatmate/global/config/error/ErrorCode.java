package com.example.eatmate.global.config.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//공통
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 에러"),
	VALIDATION_FAILED(400, "VALIDATION_FAILED", "요청 값이 올바르지 않습니다."),

	//회원
	USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),
	MEMBER_ALREADY_EXISTS(409, "MEMBER_ALREADY_EXISTS", "이미 가입된 이메일입니다."),
	DUPLICATE_PHONE_NUMBER(409, "DUPLICATE_PHONE_NUMBER", "이미 사용 중인 전화번호입니다."),
	DUPLICATE_STUDENT_NUMBER(409, "DUPLICATE_STUDENT_NUMBER", "이미 사용 중인 학번입니다."),
	DUPLICATE_NICKNAME(409, "NICKNAME_ALREADY_EXISTS", "이미 존재하는 닉네임입니다."),
	INVALID_STUDENT_NUMBER(400, "INVALID_STUDENT_NUMBER", "유효하지 않은 학번입니다."),
	INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
	INVALID_MBTI(404, "INVALID_MBTI", "유효하지 않은 MBTI입니다."),
	INVALID_GENDER(400, "INVALID_GENDER", "성별 선택은 필수입니다."),
	INVALID_PHONE_NUMBER(400, "INVALID_PHONE_NUMBER", "유효하지 않은 전화번호입니다."),

	// 모임
	INVALID_PARTICIPANT_LIMIT(400, "INVALID_PARTICIPANT_LIMIT", "올바른 참가자 수를 입력해주세요."),
	MEETING_NOT_FOUND(404, "MEETING_NOT_FOUND", "모임을 찾을 수 없습니다."),
	PARTICIPANT_LIMIT_EXCEEDED(400, "PARTICIPANT_LIMIT_EXCEEDED", "참가자 수를 초과했습니다."),
	PARTICIPANT_ALREADY_EXISTS(409, "PARTICIPANT_ALREADY_EXISTS", "이미 참여 중인 모임입니다."),
	GENDER_RESTRICTED_MEETING(403, "GENDER_RESTRICTED_MEETING", "성별 제한으로 인해 참여할 수 없는 모임입니다."),

	// 신고
	SELF_REPORT_NOT_ALLOWED(400, "SELF_REPORT_NOT_ALLOWED", "자기 자신을 신고할 수 없습니다."),
	INVALID_REPORT_TYPE_LIST(400, "INVALID_REPORT_TYPE_LIST", "올바르지 않은 신고 사유 리스트입니다."),
	DUPLICATE_REPORT_NOT_ALLOWED(400, "DUPLICATE_REPORT_NOT_ALLOWED", "같은 유저를 연속적으로 신고할 수 없습니다.");

	private final int status;
	private final String code;
	private final String message;
}
