package com.example.eatmate.global.response;

import java.time.LocalDateTime;

import com.example.eatmate.global.config.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GlobalResponseDto<T> {
	private boolean isSuccess;
	private int code;
	private LocalDateTime timestamp;
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	@Builder
	public GlobalResponseDto(boolean isSuccess, int code, LocalDateTime timestamp, String message, T result) {
		this.isSuccess = isSuccess;
		this.code = code;
		this.timestamp = timestamp;
		this.message = message;
		this.result = result;
	}

	// 201 Create 성공 응답
	public static <T> GlobalResponseDto<T> success(T result, int code) {
		return GlobalResponseDto.<T>builder()
			.isSuccess(true)
			.code(code)
			.timestamp(LocalDateTime.now())
			.message("성공")
			.result(result)
			.build();
	}

	// 성공 응답
	public static <T> GlobalResponseDto<T> success(T result) {
		return GlobalResponseDto.<T>builder()
			.isSuccess(true)
			.code(200)
			.timestamp(LocalDateTime.now())
			.message("성공")
			.result(result)
			.build();
	}

	//성공 응답 result 없이
	public static <T> GlobalResponseDto<T> success() {
		return GlobalResponseDto.<T>builder()
			.isSuccess(true)
			.code(200)
			.timestamp(LocalDateTime.now())
			.message("성공")
			.result(null)
			.build();
	}

	// 실패 응답
	public static <T> GlobalResponseDto<T> fail(ErrorCode code, String message) {
		return GlobalResponseDto.<T>builder()
			.isSuccess(false)
			.code(code.getStatus())
			.timestamp(LocalDateTime.now())
			.message(message)
			.result(null)
			.build();
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		return mapper.writeValueAsString(this);
	}
}
