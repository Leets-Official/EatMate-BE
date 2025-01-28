package com.example.eatmate.app.domain.report.domain;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.report.dto.ReportRequestDto;
import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Report extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 신고자
	@ManyToOne
	@JoinColumn(name = "reporter_member_id")
	private Member reporter;

	// 신고 받은 사람
	@ManyToOne
	@JoinColumn(name = "reported_member_id")
	private Member reported;

	// 신고 유형 목록
	@NotNull
	@Enumerated(EnumType.STRING)
	private ReportType reportType;

	// 구체적인 신고 사유
	@Column(nullable = false)
	private String reportingReasonDescription;

	@NotBlank
	private String chatMessage;

	// 처리 여부
	@Column(nullable = false)
	private boolean isProcessed = false;

	@Builder
	private Report(Member reporter, Member reported, ReportType reportType, String reportingReasonDescription,
		String chatMessage) {
		this.reporter = reporter;
		this.reported = reported;
		this.reportType = reportType;
		this.reportingReasonDescription = reportingReasonDescription;
		this.chatMessage = chatMessage;
		this.isProcessed = false;
	}

	public static Report createReport(ReportRequestDto reportRequestDto, Member reporter, Member reported) {
		return Report.builder()
			.reporter(reporter)
			.reported(reported)
			.reportType(reportRequestDto.getReportType())
			.reportingReasonDescription(reportRequestDto.getReportingReasonDescription())
			.chatMessage(reportRequestDto.getChatMessage())
			.build();
	}

}
