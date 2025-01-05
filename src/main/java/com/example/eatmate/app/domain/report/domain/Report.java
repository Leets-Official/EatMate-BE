package com.example.eatmate.app.domain.report.domain;

import java.util.List;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.report.converter.ReportListConverter;
import com.example.eatmate.app.domain.report.dto.ReportRequestDto;
import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@Convert(converter = ReportListConverter.class)
	private List<ReportType> reportTypes;

	// 구체적인 신고 사유
	@Column(nullable = false)
	private String reportingReasonDescription;

	// 처리 여부
	@Column(nullable = false)
	private boolean processed = false;

	@Builder
	public Report(Member reporter, Member reported, List<ReportType> reportTypes, String reportingReasonDescription) {
		this.reporter = reporter;
		this.reported = reported;
		this.reportTypes = reportTypes;
		this.reportingReasonDescription = reportingReasonDescription;
		this.processed = false;
	}

	public static Report createReport(ReportRequestDto reportRequestDto, Member reporter, Member reported) {
		return Report.builder()
			.reporter(reporter)
			.reported(reported)
			.reportTypes(reportRequestDto.getReportTypes())
			.reportingReasonDescription(reportRequestDto.getReportingReasonDescription())
			.build();
	}

}
