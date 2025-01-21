package com.example.eatmate.global.config.error.exception.custom;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

public class InvalidMbtiException extends CommonException {
	public InvalidMbtiException() {

		super(ErrorCode.INVALID_MBTI);
	}
}
