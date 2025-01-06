package com.example.eatmate.app.domain.report.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.app.domain.report.domain.Report;
import com.example.eatmate.app.domain.report.domain.repository.ReportRepository;
import com.example.eatmate.app.domain.report.dto.ReportAdminResponseDto;
import com.example.eatmate.app.domain.report.dto.ReportRequestDto;
import com.example.eatmate.app.domain.report.dto.ReportResponseDto;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
	private final ReportRepository reportRepository;

	private final MemberRepository memberRepository;

	// 신고하기 메소드
	@Transactional
	public void createReport(ReportRequestDto reportRequestDto, String email) {

		Member member = findMemberByEmail(email);
		Member reported = findMemberByEmail(reportRequestDto.getReportedMemberEmail());

		if (member.equals(reported)) {
			throw new CommonException(ErrorCode.SELF_REPORT_NOT_ALLOWED);
		}

		Report report = Report.createReport(reportRequestDto, member, reported);
		reportRepository.save(report);
	}

	// 내 신고 내역 불러오기
	@Transactional(readOnly = true)
	public List<ReportResponseDto> getMyReports(String email) {

		List<Report> myReports = reportRepository.findAllByReporterEmail(email);
		return myReports.stream()
			.map(report -> new ReportResponseDto(
				report.getReported().getName(),
				report.getReportTypes(),
				report.getReportingReasonDescription(),
				report.getCreatedAt(),
				report.isProcessed()
			))
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ReportAdminResponseDto> getAllReportsByAdmin() {

		List<Report> myReports = reportRepository.findAll();
		return myReports.stream()
			.map(report -> new ReportAdminResponseDto(
				report.getReporter().getName(),
				report.getReporter().getEmail(),
				report.getReported().getName(),
				report.getReported().getEmail(),
				report.getReportTypes(),
				report.getReportingReasonDescription(),
				report.getCreatedAt(),
				report.isProcessed()
			))
			.toList();
	}

	private Member findMemberByEmail(String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));
	}

}
