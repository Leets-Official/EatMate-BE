package com.example.eatmate.global.config.error.exception.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IssueCreateRequestDto {
	private final String title;           // 이슈 제목
	private final String body;            // 이슈 본문
	private final List<String> assignees; // 할당할 담당자들
	private final List<String> labels;    // 이슈 라벨들
}
