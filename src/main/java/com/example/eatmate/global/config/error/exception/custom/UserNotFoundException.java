package com.example.eatmate.global.config.error.exception.custom;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

public class UserNotFoundException extends CommonException {
	public UserNotFoundException() {

		super(ErrorCode.USER_NOT_FOUND);
	}
}
