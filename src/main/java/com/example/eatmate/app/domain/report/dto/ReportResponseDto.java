package com.example.eatmate.app.domain.report.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.report.domain.ReportType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReportResponseDto {

	private String reportedUserName;

	private List<ReportType> reportTypes;

	private String reportingReasonDescription;

	private LocalDateTime time;

	private boolean isProcessed;

}
