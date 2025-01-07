package com.example.eatmate.app.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NoticeAdminRequestDto {

	@NotBlank
	private String title;

	@NotBlank
	private String content;

}
