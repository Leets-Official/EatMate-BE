package com.example.eatmate.global.config.error.exception.custom;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

public class SelfReportingNotAllowedException extends CommonException {
	public SelfReportingNotAllowedException() {

		super(ErrorCode.SELF_REPORT_NOT_ALLOWED);
	}
}
