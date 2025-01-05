package com.example.eatmate.app.domain.report.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.eatmate.app.domain.report.dto.ReportAdminResponseDto;
import com.example.eatmate.app.domain.report.service.ReportService;
import com.example.eatmate.global.response.GlobalResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportAdminController {

	private final ReportService reportService;

	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "어드민만 볼수있는 전체 신고 내역", description = "관리자가 신고 내역을 불러옵니다.")
	public ResponseEntity<GlobalResponseDto<List<ReportAdminResponseDto>>> getAllReports() {
		return ResponseEntity.status(HttpStatus.OK)
			.body(GlobalResponseDto.success(reportService.getAllReportsByAdmin()));
	}

}
