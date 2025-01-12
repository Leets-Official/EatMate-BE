package com.example.eatmate.app.domain.notice.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.notice.domain.Notice;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeResponseDto {

	private final Long noticeId;

	private final String title;

	private final String content;

	private final LocalDateTime createdAt;

	private final LocalDateTime updatedAt;

	@Builder
	private NoticeResponseDto(Long noticeId, String title, String content,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.noticeId = noticeId;
		this.title = title;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static NoticeResponseDto from(Notice notice) {
		return NoticeResponseDto.builder()
			.noticeId(notice.getId())
			.title(notice.getTitle())
			.content(notice.getContent())
			.createdAt(notice.getCreatedAt())
			.updatedAt(notice.getUpdatedAt())
			.build();
	}
}
