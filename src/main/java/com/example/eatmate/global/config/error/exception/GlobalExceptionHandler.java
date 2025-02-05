package com.example.eatmate.global.config.error.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.ErrorResponse;
import com.example.eatmate.global.response.GlobalResponseDto;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private static final String ISSUE_CREATE_ENV = "dev";
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

	@ExceptionHandler(CommonException.class)
	public Object handleCommonException(CommonException ex, RedirectAttributes redirectAttributes) {
		ErrorCode errorCode = ex.getErrorCode();

		// 이메일 도메인 에러일 경우 리다이렉트
		if (errorCode == ErrorCode.INVALID_EMAIL_DOMAIN) {
			redirectAttributes.addFlashAttribute("errorMessage", errorCode.getMessage());
			return "redirect:https://develop.d4u0qurydeei4.amplifyapp.com/intro/oauth2/invalid-account";
		}

		// 그 외의 경우 API 응답
		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, new ErrorResponse(errorCode).getMessage()));
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

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<GlobalResponseDto<String>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException ex) {
		ErrorCode errorCode = ErrorCode.INVALID_PARAMETER_TYPE;

		String paramName = ex.getName();
		String errorMessage = String.format("파라미터 '%s' 가 적절하지 않은 값을 가지고 있습니다.: %s",
			paramName,
			ex.getValue());

		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorMessage));
	}

	@ExceptionHandler(ConversionFailedException.class)
	public ResponseEntity<GlobalResponseDto<String>> handleConversionFailedException(
		ConversionFailedException ex) {
		ErrorCode errorCode = ErrorCode.INVALID_PARAMETER_TYPE;

		String targetType = ex.getTargetType().getType().getSimpleName();
		String value = String.valueOf(ex.getValue());
		String errorMessage = String.format("ENUM '%s'에 '%s' 값이 존재하지 않습니다.",
			targetType,
			value);

		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorMessage));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<GlobalResponseDto<String>> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException ex) {
		ErrorCode errorCode = ErrorCode.INVALID_PARAMETER_TYPE;

		String parameterName = ex.getParameterName();
		String errorMessage = String.format("필수 파라미터 '%s'가 누락되었습니다.",
			parameterName
		);

		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorMessage));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<GlobalResponseDto<String>> handleDataIntegrityViolationException(
		DataIntegrityViolationException ex) {
		ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
		String errorMessage = "필수 입력 필드가 누락되었습니다.";

		return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(GlobalResponseDto.fail(errorCode, errorMessage));
	}

	@ExceptionHandler(HttpMessageConversionException.class)
	public ResponseEntity<GlobalResponseDto<String>> handleHttpMessageConversionException(
		HttpMessageConversionException ex) {
		ErrorCode errorCode = ErrorCode.INVALID_REQUEST_FORMAT;
		String errorMessage = "잘못된 요청 형식입니다. multipart/form-data 형식으로 요청해주세요.";

		// MultipartFile 변환 실패 관련 에러인 경우
		if (ex.getCause() instanceof InvalidDefinitionException
			&& ex.getMessage().contains("MultipartFile")) {
			errorMessage = "파일 업로드는 multipart/form-data 형식으로 요청해주세요.";
		}

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
