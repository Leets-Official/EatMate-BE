package com.example.eatmate.app.domain.notice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.notice.dto.NoticeAdminRequestDto;
import com.example.eatmate.app.domain.notice.service.NoticeService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class NoticeAdminController {

	private final NoticeService noticeService;

	@PostMapping
	@Operation(summary = "관리자 공지사항 작성", description = "관리자가 공지사항을 작성합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> save(
		@RequestBody @Valid NoticeAdminRequestDto noticeAdminRequestDto) {
		noticeService.createNotice(noticeAdminRequestDto);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());

	}

	@PatchMapping("/{noticeId}")
	@Operation(summary = "관리자 공지사항 수정", description = "관리자가 공지사항을 수정합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> update(@PathVariable Long noticeId,
		@RequestBody @Valid NoticeAdminRequestDto noticeAdminRequestDto) {
		noticeService.updateNotice(noticeId, noticeAdminRequestDto);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}

	@DeleteMapping("/{noticeId}")
	@Operation(summary = "관리자 공지사항 삭제", description = "관리자가 공지사항을 삭제합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> delete(@PathVariable Long noticeId) {
		noticeService.deleteNotice(noticeId);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}

}
