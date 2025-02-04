package com.example.eatmate.app.domain.report.dto;

import com.example.eatmate.app.domain.report.domain.ReportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReportRequestDto {

	@NotNull(message = "신고 유형은 필수이며, OFFENSIVE, HARASSMENT, OTHER 중 하나여야 합니다.")
	private ReportType reportType;

	@NotBlank(message = "신고 사유를 입력해 주세요.")
	private String reportingReasonDescription;    // 구체적인 신고 사유

	@NotNull(message = "신고할 사용자를 지정해 주세요.")
	private Long reportedMemberId;

	@NotBlank(message = "채팅 메세지가 비어있을 수 없습니다.")
	private String chatMessage;

}
