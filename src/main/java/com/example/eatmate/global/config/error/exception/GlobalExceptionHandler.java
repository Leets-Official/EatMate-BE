package com.example.eatmate.global.config.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.response.GlobalResponseDto;
import com.example.eatmate.global.config.error.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	private static void showErrorLog(ErrorCode errorCode) {
		log.error("errorCode: {}, message: {}", errorCode.getCode(), errorCode.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<GlobalResponseDto> handleGenericException(Exception ex) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse errorResponse = new ErrorResponse(errorCode);
		log.error(ex.getMessage());
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

}
