package com.example.eatmate.app.domain.notice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.notice.dto.NoticeAdminRequestDto;
import com.example.eatmate.app.domain.notice.service.NoticeService;
import com.example.eatmate.global.response.GlobalResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class NoticeAdminController {

	private final NoticeService noticeService;

	@PostMapping
	public ResponseEntity<GlobalResponseDto<Void>> save(
		@RequestBody @Valid NoticeAdminRequestDto noticeAdminRequestDto) {

		noticeService.createNotice(noticeAdminRequestDto);

		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());

	}

}
