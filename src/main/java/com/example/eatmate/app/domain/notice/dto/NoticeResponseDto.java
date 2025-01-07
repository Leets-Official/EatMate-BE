package com.example.eatmate.app.domain.notice.dto;

import java.time.LocalDateTime;

import com.example.eatmate.app.domain.notice.domain.Notice;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeResponseDto {

	Long noticeId;

	String title;

	String content;

	LocalDateTime createdAt;

	LocalDateTime updatedAt;

	@Builder
	private NoticeResponseDto(Notice notice) {
		this.noticeId = notice.getId();
		this.title = notice.getTitle();
		this.content = notice.getContent();
		this.createdAt = notice.getCreatedAt();
		this.updatedAt = notice.getUpdatedAt();
	}

	public static NoticeResponseDto createNoticeResponseDto(Notice notice) {
		return NoticeResponseDto.builder()
			.notice(notice)
			.build();
	}
}
