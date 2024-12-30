package com.example.eatmate.global.config.error.exception.custom;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

public class InvalidTokenException extends CommonException {
    public InvalidTokenException() {

            super(ErrorCode.INVALID_TOKEN);
    }
}
