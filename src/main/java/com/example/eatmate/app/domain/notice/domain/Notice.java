package com.example.eatmate.app.domain.notice.domain;

import com.example.eatmate.app.domain.notice.dto.NoticeAdminRequestDto;
import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Notice extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Builder
	private Notice(NoticeAdminRequestDto noticeAdminRequestDto) {
		this.title = noticeAdminRequestDto.getTitle();
		this.content = noticeAdminRequestDto.getContent();
	}

	public static Notice createNotice(NoticeAdminRequestDto noticeAdminRequestDto) {
		return Notice.builder()
			.noticeAdminRequestDto(noticeAdminRequestDto)
			.build();
	}

}
