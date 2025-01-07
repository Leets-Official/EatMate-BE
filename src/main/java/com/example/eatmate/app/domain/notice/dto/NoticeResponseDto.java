package com.example.eatmate.app.domain.notice.dto;

import com.example.eatmate.app.domain.notice.domain.Notice;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeResponseDto {
	Long noticeId;
	String title;
	String content;

	@Builder
	private NoticeResponseDto(Notice notice) {
		this.noticeId = notice.getId();
		this.title = notice.getTitle();
		this.content = notice.getContent();
	}

	public static NoticeResponseDto createNoticeResponseDto(Notice notice) {
		return NoticeResponseDto.builder()
			.notice(notice)
			.build();
	}
}
