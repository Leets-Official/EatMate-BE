package com.example.eatmate.app.domain.report.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.report.dto.ReportMemberListResponseDto;
import com.example.eatmate.app.domain.report.dto.ReportRequestDto;
import com.example.eatmate.app.domain.report.service.ReportService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping
	@Operation(summary = "유저 신고하기", description = "해당 유저를 신고합니다.")
	public ResponseEntity<GlobalResponseDto<Void>> reportUser(
		@RequestBody @Valid ReportRequestDto reportRequestDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		reportService.createReport(reportRequestDto, userDetails);
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success());
	}

	@GetMapping("/all")
	@Operation(summary = "신고 내역 불러오기", description = "신고 내역을 불러옵니다.")
	public ResponseEntity<GlobalResponseDto<List<ReportMemberListResponseDto>>> getMyReports(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(reportService.getMyReports(userDetails)));
	}
}
