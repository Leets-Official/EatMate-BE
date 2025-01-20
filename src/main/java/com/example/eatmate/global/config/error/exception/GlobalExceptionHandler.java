package com.example.eatmate.global.config.error.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.ErrorResponse;
import com.example.eatmate.global.response.GlobalResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
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
		log.error(ex.getMessage(), ex);
		log.error(ex.getClass().getSimpleName());

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

	@ExceptionHandler(ConstraintViolationException.class) // 파라미터 검증 실패
	public ResponseEntity<GlobalResponseDto<String>> handleConstraintViolationException(
		ConstraintViolationException ex) {
		ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;

		String errorMessage = ex.getConstraintViolations()
			.stream()
			.map(violation -> violation.getMessage())
			.collect(Collectors.joining(", "));

		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorMessage));
	}

	@ExceptionHandler(NoResourceFoundException.class) // 리소스를 찾을 수 없을 때
	public ResponseEntity<GlobalResponseDto<String>> handleNoResourceFoundException(NoResourceFoundException ex,
		HttpServletRequest request) {
		ErrorCode errorCode = ErrorCode.NO_RESOURCE_FOUND;
		ErrorResponse errorResponse = new ErrorResponse(errorCode);

		// 요청된 URI와 전체 URL을 로그에 기록
		String requestUri = request.getRequestURI();
		String requestUrl = request.getRequestURL().toString();

		if (requestUri.contains("favicon.ico")) {
			log.error("Favicon request not found - URI: {}, Full URL: {}", requestUri, requestUrl);
		} else {
			log.error("Resource not found - URI: {}, Full URL: {}", requestUri, requestUrl);
		}
		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorResponse.getMessage()));
	}

	@ExceptionHandler(TransactionSystemException.class) // 트랜잭션 처리 중 예외 발생
	public ResponseEntity<GlobalResponseDto<String>> handleTransactionSystemException(TransactionSystemException ex) {
		ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
		Throwable cause = ex.getCause();

		if (cause instanceof ConstraintViolationException) {
			return handleConstraintViolationException((ConstraintViolationException)cause);
		}

		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorCode.getMessage()));
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
