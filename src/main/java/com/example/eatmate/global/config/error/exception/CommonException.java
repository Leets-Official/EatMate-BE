package com.example.eatmate.global.config.error.exception;

import com.example.eatmate.global.config.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonException extends RuntimeException {
	private final ErrorCode errorCode;
}
