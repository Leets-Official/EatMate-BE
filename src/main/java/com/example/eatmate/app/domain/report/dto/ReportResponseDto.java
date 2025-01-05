package com.example.eatmate.app.domain.report.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.report.domain.ReportType;

import lombok.Getter;

@Getter
public class ReportResponseDto {
	private String reportedUserName;
	private List<ReportType> reportTypes;
	private String reportingReasonDescription;
	private LocalDateTime time;
	private boolean processed;

	public ReportResponseDto(String reportedUserName, List<ReportType> reportTypes,
		String reportingReasonDescription, LocalDateTime time, boolean processed) {
		this.reportedUserName = reportedUserName;
		this.reportTypes = reportTypes;
		this.reportingReasonDescription = reportingReasonDescription;
		this.time = time;
		this.processed = processed;
	}
}
