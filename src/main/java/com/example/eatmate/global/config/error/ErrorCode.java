package com.example.eatmate.global.config.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//공통
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 에러"),
	VALIDATION_FAILED(400, "VALIDATION_FAILED", "요청 값이 올바르지 않습니다."),
	JSON_PARSING_ERROR(400, "JSON_PARSING_ERROR", "JSON 데이터 처리 중 오류가 발생했습니다"),
	NO_RESOURCE_FOUND(404, "NO_RESOURCE_FOUND", "해당 리소스를 찾을 수 없습니다."),
	INVALID_EMAIL_DOMAIN(400, "INVALID_EMAIL_DOMAIN", "가천대학교 이메일이 아닙니다."),

	//회원
	USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),
	TOKEN_NOT_FOUND(404, "TOKEN_NOT_FOUND", "토큰를 찾을 수 없습니다."),
	MEMBER_ALREADY_EXISTS(409, "MEMBER_ALREADY_EXISTS", "이미 가입된 이메일입니다."),
	DUPLICATE_PHONE_NUMBER(409, "DUPLICATE_PHONE_NUMBER", "이미 사용 중인 전화번호입니다."),
	DUPLICATE_STUDENT_NUMBER(409, "DUPLICATE_STUDENT_NUMBER", "이미 사용 중인 학번입니다."),
	DUPLICATE_NICKNAME(409, "NICKNAME_ALREADY_EXISTS", "이미 존재하는 닉네임입니다."),
	INVALID_STUDENT_NUMBER(400, "INVALID_STUDENT_NUMBER", "유효하지 않은 학번입니다."),
	INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
	INVALID_MBTI(404, "INVALID_MBTI", "유효하지 않은 MBTI입니다."),
	INVALID_GENDER(400, "INVALID_GENDER", "성별 선택은 필수입니다."),
	INVALID_PHONE_NUMBER(400, "INVALID_PHONE_NUMBER", "유효하지 않은 전화번호입니다."),
	INVALID_LOGIN_INFO(401, "INVALID_LOGIN_INFO", "로그인 정보가 올바르지 않습니다."),
	BLOCKED_MEMBER_CANNOT_JOIN(403, "BLOCKED_MEMBER_CANNOT_JOIN", "차단된 사용자가 있는 모임에는 참여할 수 없습니다."),

	// 모임
	INVALID_PARTICIPANT_LIMIT(400, "INVALID_PARTICIPANT_LIMIT", "올바른 참가자 수를 입력해주세요."),
	MEETING_NOT_FOUND(404, "MEETING_NOT_FOUND", "모임을 찾을 수 없습니다."),
	PARTICIPANT_LIMIT_EXCEEDED(400, "PARTICIPANT_LIMIT_EXCEEDED", "참가자 수를 초과했습니다."),
	PARTICIPANT_ALREADY_EXISTS(409, "PARTICIPANT_ALREADY_EXISTS", "이미 참여 중인 모임입니다."),
	GENDER_RESTRICTED_MEETING(403, "GENDER_RESTRICTED_MEETING", "성별 제한으로 인해 참여할 수 없는 모임입니다."),
	INVALID_GENDER_RESTRICTION(400, "INVALID_GENDER_RESTRICTION", "올바른 성별 제한을 입력해주세요."),
	INVALID_SORT_TYPE(400, "INVALID_SORT_TYPE", "올바른 정렬 타입을 입력해주세요."),
	NOT_MEETING_HOST(403, "NOT_MEETING_HOST", "모임의 호스트만 가능한 동작입니다."),
	ALREADY_DELETED_MEETING(400, "ALREADY_DELETED_MEETING", "이미 삭제된 모임입니다."),
	CANNOT_DELETE_MEETING_WITH_PARTICIPANTS(400, "CANNOT_DELETE_MEETING_WITH_PARTICIPANTS", "참가자가 있는 모임은 삭제할 수 없습니다."),
	INVALID_MEETING_TYPE(400, "INVALID_MEETING_TYPE", "올바르지 않은 모임 타입입니다."),

	// 신고
	SELF_REPORT_NOT_ALLOWED(400, "SELF_REPORT_NOT_ALLOWED", "자기 자신을 신고할 수 없습니다."),
	INVALID_REPORT_TYPE_LIST(400, "INVALID_REPORT_TYPE_LIST", "올바르지 않은 신고 사유 리스트입니다."),
	DUPLICATE_REPORT_NOT_ALLOWED(400, "DUPLICATE_REPORT_NOT_ALLOWED", "같은 유저를 연속적으로 신고할 수 없습니다."),

	// 공지사항
	NOTICE_NOT_FOUND(404, "NOTICE_NOT_FOUND", "해당 공지사항을 찾을 수 없습니다."),

	//채팅
	CHATROOM_NOT_FOUND(404, "CHATROOM_NOT_FOUND", "채팅방을 찾을 수 없습니다."),
	MEMBER_CHATROOM_NOT_FOUND(404, "MEMBER_CHATROOM_NOT_FOUND", "멤버채팅방을 찾을 수 없습니다."),
	CHAT_NOT_FOUND(404, "CHAT_NOT_FOUND", "채팅을 찾을 수 없습니다."),
	QUEUE_NOT_EXIST(404, "QUEUE_NOT_EXIST", "큐를 찾을 수 없습니다."),

	// 이미지
	WRONG_IMAGE_FORMAT(400, "WRONG_IMAGE_FORMAT", "지원되지 않는 확장자 입니다. jpg, jpeg, png 파일만 업로드할 수 있습니다"),
	IMAGE_UPLOAD_FAIL(400, "IMAGE_UPLOAD_FAIL", "이미지 업로드 중에 오류가 발생하였습니다."),

	// 차단
	MEETING_ALREADY_BLOCKED(400, "MEETING_ALREADY_BLOCKED", "이미 차단한 모임입니다."),
	MEMBER_ALREADY_BLOCKED(400, "MEMBER_ALREADY_BLOCKED", "이미 차단한 멤버입니다."),
	MEMBER_NOT_BLOCKED(400, "MEMBER_NOT_BLOCKED", "차단하지 않은 멤버를 차단 해제할 수 없습니다");

	private final int status;
	private final String code;
	private final String message;
}
