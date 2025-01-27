package com.example.eatmate.app.domain.report.dto;

import com.example.eatmate.app.domain.report.domain.ReportType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReportRequestDto {

	@NotNull(message = "신고 유형이 비어있을 수 없습니다.")
	@JsonProperty("reportType")
	private ReportType reportType;

	@NotBlank(message = "신고 사유는 비어있을 수 없습니다.")
	private String reportingReasonDescription;    // 구체적인 신고 사유

	@NotNull(message = "신고 대상이 비어있을 수 없습니다.")
	private Long reportedMemberId;

	@NotBlank(message = "채팅 메세지가 비어있을 수 없습니다.")
	private String chatMessage;

}
