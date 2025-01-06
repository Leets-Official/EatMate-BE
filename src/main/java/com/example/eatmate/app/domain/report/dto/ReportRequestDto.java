package com.example.eatmate.app.domain.report.dto;

import java.util.List;

import com.example.eatmate.app.domain.report.domain.ReportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class ReportRequestDto {

	@NotEmpty
	private List<ReportType> reportTypes;

	@NotBlank
	private String reportingReasonDescription;    // 구체적인 신고 사유

	@NotBlank
	private String reportedMemberEmail;

}
