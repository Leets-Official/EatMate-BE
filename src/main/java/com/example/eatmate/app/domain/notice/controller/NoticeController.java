package com.example.eatmate.app.domain.notice.controller;

import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.notice.dto.NoticeResponseDto;
import com.example.eatmate.app.domain.notice.service.NoticeService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

	private final NoticeService noticeService;

	@GetMapping("/{noticeId}")
	@Operation(summary = "단일 공지사항 조회", description = "단일 공지사항을 조회합니다.")
	public ResponseEntity<GlobalResponseDto<NoticeResponseDto>> getNotice(@PathVariable Long noticeId) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(noticeService.findNotice(noticeId)));
	}

	@GetMapping
	@Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 페이지별로 조회합니다..")
	@Parameter(name = "pageNumber", description = "조회할 페이지 번호 (0부터 시작, 기본값: 0)", required = false)
	@Parameter(name = "pageSize", description = "페이지당 조회할 공지사항 수 (기본값: 20, 최대: 100)", required = false)
	public ResponseEntity<GlobalResponseDto<Slice<NoticeResponseDto>>> getNotices(
		@RequestParam(defaultValue = "0") @PositiveOrZero int pageNumber,
		@RequestParam(defaultValue = "20") @Positive @Max(100) int pageSize) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(noticeService.findNotices(pageNumber, pageSize)));
	}

}
