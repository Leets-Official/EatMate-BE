package com.example.eatmate.global.config.error.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.ErrorResponse;
import com.example.eatmate.global.response.GlobalResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private static final String ISSUE_CREATE_ENV = "dev";
	private final View error;
	private final GithubIssueGenerator githubIssueGenerator;
	private final Environment environment;

	private static void showErrorLog(ErrorCode errorCode) {
		log.error("errorCode: {}, message: {}", errorCode.getCode(), errorCode.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<GlobalResponseDto> handleGenericException(Exception ex) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse errorResponse = new ErrorResponse(errorCode);
		log.error(ex.getMessage());
		handleUnexpectedError(ex);
		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorResponse.getMessage()));
	}

	@ExceptionHandler(CommonException.class) // Custom Exception을 포괄적으로 처리
	public ResponseEntity<GlobalResponseDto<String>> handleCommonException(CommonException ex) {
		ErrorCode errorCode = ex.getErrorCode(); // 전달된 예외에서 에러 코드 가져오기
		ErrorResponse errorResponse = new ErrorResponse(errorCode);
		showErrorLog(errorCode);
		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorResponse.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class) // Valid 검증 실패시 예외처리
	public ResponseEntity<GlobalResponseDto<String>> handleValidationExceptions(
		MethodArgumentNotValidException ex) {
		ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;

		// 모든 검증 오류를 하나의 문자열로 결합
		String errorMessage = ex.getBindingResult()
			.getAllErrors()
			.stream()
			.map(error -> error.getDefaultMessage())
			.collect(Collectors.joining(", "));

		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorMessage));
	}

	public void handleUnexpectedError(Exception ex) {
		if (isProd(environment.getActiveProfiles())) {
			githubIssueGenerator.create(ex);
		}
	}

	private boolean isProd(String[] activeProfiles) {
		return Arrays.stream(activeProfiles).anyMatch(profile -> profile.equalsIgnoreCase(ISSUE_CREATE_ENV));
	}
}
