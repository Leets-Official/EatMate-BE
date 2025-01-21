package com.example.eatmate.app.domain.report.dto;

import com.example.eatmate.app.domain.report.domain.ReportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReportRequestDto {

	@NotNull
	private ReportType reportType;

	@NotBlank
	private String reportingReasonDescription;    // 구체적인 신고 사유

	@NotBlank
	private String reportedMemberEmail;

}
