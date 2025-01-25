package com.example.eatmate.app.domain.report.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.report.domain.Report;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportMemberListResponseDto {
	private final Long reportId;

	private final Long reportedMemberId;

	private final String reportedUserName;

	private final String profileImageUrl;

	private final String chatMessage;

	private final LocalDateTime time;

	private final boolean isProcessed;

	@Builder
	private ReportMemberListResponseDto(Long reportId, Long reportedMemberId, String reportedUserName,
		String profileImageUrl, LocalDateTime time, String chatMessage,
		boolean isProcessed) {
		this.reportId = reportId;
		this.reportedMemberId = reportedMemberId;
		this.reportedUserName = reportedUserName;
		this.profileImageUrl = profileImageUrl;
		this.time = time;
		this.isProcessed = isProcessed;
		this.chatMessage = chatMessage;
	}

	public static ReportMemberListResponseDto createReportResponseDto(Report report) {
		return ReportMemberListResponseDto.builder()
			.reportId(report.getId())
			.reportedMemberId(report.getReported().getMemberId())
			.reportedUserName(report.getReported().getNickname())
			.profileImageUrl(report.getReported().getProfileImage() != null ?
				report.getReported().getProfileImage().getImageUrl() : null)
			.time(report.getCreatedAt())
			.isProcessed(report.isProcessed())
			.chatMessage(report.getChatMessage())
			.build();
	}

}
