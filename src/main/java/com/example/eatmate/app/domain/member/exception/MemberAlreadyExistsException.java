package com.example.eatmate.app.domain.member.exception;

import com.example.eatmate.global.config.error.ErrorCode;

public class MemberAlreadyExistsException extends RuntimeException {
   private final ErrorCode errorCode;

    public MemberAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
