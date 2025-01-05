package com.example.eatmate.app.domain.report.dto;

import java.util.List;

import com.example.eatmate.app.domain.report.domain.ReportType;

import lombok.Getter;

@Getter
public class ReportRequestDto {
	private List<ReportType> reportTypes;
	private String reportingReasonDescription;
	private String reportedMemberEmail;
}
