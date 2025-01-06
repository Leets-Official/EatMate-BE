package com.example.eatmate.app.domain.report.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.report.domain.ReportType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportAdminResponseDto {

	// 신고한 유저 정보
	private String reporterName;
	private String reporterEmail;
	
	// 신고 받은 유저 정보
	private String reportedName;
	private String reportedEmail;

	private List<ReportType> reportTypes;

	private String reportingReasonDescription;

	private LocalDateTime time;

	private boolean processed;

}
