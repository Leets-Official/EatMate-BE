package com.example.eatmate.global.config.error.exception.custom;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

public class MemberAlreadyExistsException extends CommonException {

    public MemberAlreadyExistsException() {

        super(ErrorCode.MEMBER_ALREADY_EXISTS);
    }


}
