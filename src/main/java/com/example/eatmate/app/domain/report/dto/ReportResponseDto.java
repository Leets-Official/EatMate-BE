package com.example.eatmate.app.domain.report.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.report.domain.Report;
import com.example.eatmate.app.domain.report.domain.ReportType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportResponseDto {

	private String reportedUserName;

	private List<ReportType> reportTypes;

	private String reportingReasonDescription;

	private LocalDateTime time;

	private boolean isProcessed;

	@Builder
	private ReportResponseDto(Report report) {
		this.reportedUserName = report.getReported().getNickname();
		this.reportTypes = report.getReportTypes();
		this.reportingReasonDescription = report.getReportingReasonDescription();
		this.time = report.getCreatedAt();
		this.isProcessed = report.isProcessed();
	}

	public static ReportResponseDto createReportResponseDto(Report report) {
		return ReportResponseDto.builder()
			.report(report)
			.build();
	}

}
