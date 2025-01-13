package com.example.eatmate.app.domain.notice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeIdResponseDto {
	private final Long noticeId;

	@Builder
	NoticeIdResponseDto(Long noticeId) {
		this.noticeId = noticeId;
	}

	public static NoticeIdResponseDto from(Long noticeId) {
		return NoticeIdResponseDto.builder()
			.noticeId(noticeId)
			.build();
	}
}
