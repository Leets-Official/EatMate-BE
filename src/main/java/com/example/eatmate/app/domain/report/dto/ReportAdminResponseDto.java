package com.example.eatmate.app.domain.report.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.report.domain.Report;
import com.example.eatmate.app.domain.report.domain.ReportType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportAdminResponseDto {
	private final Long reportId;

	// 신고한 유저 정보
	private final Long reporterMemberId;
	private final String reporterName;
	private final String reporterEmail;

	// 신고 받은 유저 정보
	private final Long reportedMemberId;
	private final String reportedName;
	private final String reportedEmail;

	private final ReportType reportType;

	private final String reportingReasonDescription;

	private final LocalDateTime time;

	private final boolean isProcessed;

	@Builder
	private ReportAdminResponseDto(Long reportId, Long reporterMemberId, String reporterName, String reporterEmail,
		Long reportedMemberId, String reportedName, String reportedEmail, ReportType reportType,
		String reportingReasonDescription, LocalDateTime time, boolean isProcessed) {
		this.reportId = reportId;
		this.reporterMemberId = reporterMemberId;
		this.reporterName = reporterName;
		this.reporterEmail = reporterEmail;
		this.reportedMemberId = reportedMemberId;
		this.reportedName = reportedName;
		this.reportedEmail = reportedEmail;
		this.reportType = reportType;
		this.reportingReasonDescription = reportingReasonDescription;
		this.time = time;
		this.isProcessed = isProcessed;
	}

	public static ReportAdminResponseDto createReportAdminResponseDto(Report report) {
		return ReportAdminResponseDto.builder()
			.reportId(report.getId())
			.reporterMemberId(report.getReporter().getMemberId())
			.reporterName(report.getReporter().getNickname())
			.reporterEmail(report.getReporter().getEmail())
			.reportedMemberId(report.getReported().getMemberId())
			.reportedName(report.getReported().getNickname())
			.reportedEmail(report.getReported().getEmail())
			.reportType(report.getReportType())
			.reportingReasonDescription(report.getReportingReasonDescription())
			.time(report.getCreatedAt())
			.isProcessed(report.isProcessed())
			.build();
	}

}
