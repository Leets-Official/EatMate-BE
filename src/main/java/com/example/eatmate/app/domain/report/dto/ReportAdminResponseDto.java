package com.example.eatmate.app.domain.report.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.report.domain.Report;
import com.example.eatmate.app.domain.report.domain.ReportType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportAdminResponseDto {

	// 신고한 유저 정보
	private String reporterName;
	private String reporterEmail;
	private Long reporterId;

	// 신고 받은 유저 정보
	private String reportedName;
	private String reportedEmail;
	private Long reportedId;

	private List<ReportType> reportTypes;

	private String reportingReasonDescription;

	private LocalDateTime time;

	private boolean isProcessed;

	@Builder
	private ReportAdminResponseDto(Report report) {
		this.reporterName = report.getReporter().getNickname();
		this.reporterEmail = report.getReporter().getEmail();
		this.reporterId = report.getReporter().getMemberId();
		this.reportedName = report.getReported().getNickname();
		this.reportedEmail = report.getReported().getEmail();
		this.reportedId = report.getReported().getMemberId();
		this.reportTypes = report.getReportTypes();
		this.reportingReasonDescription = report.getReportingReasonDescription();
		this.time = report.getCreatedAt();
		this.isProcessed = isProcessed();
	}

	public static ReportAdminResponseDto createReportAdminResponseDto(Report report) {
		return ReportAdminResponseDto.builder()
			.report(report)
			.build();
	}

}
