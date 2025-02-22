package com.example.eatmate.app.domain.report.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.app.domain.report.domain.Report;
import com.example.eatmate.app.domain.report.domain.repository.ReportRepository;
import com.example.eatmate.app.domain.report.dto.ReportAdminResponseDto;
import com.example.eatmate.app.domain.report.dto.ReportMemberListResponseDto;
import com.example.eatmate.app.domain.report.dto.ReportRequestDto;
import com.example.eatmate.global.common.util.SecurityUtils;
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

	private final SecurityUtils securityUtils;

	// 신고하기 메소드
	@Transactional
	public void createReport(ReportRequestDto reportRequestDto, UserDetails userDetails) {

		Member member = securityUtils.getMember(userDetails);
		Member reported = memberRepository.findById(reportRequestDto.getReportedMemberId()).orElseThrow(
			() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		if (member.equals(reported)) {
			throw new CommonException(ErrorCode.SELF_REPORT_NOT_ALLOWED);
		}

		List<Report> reports = reportRepository.findAllByReporterMemberIdAndReportedMemberId(member.getMemberId(),
			reported.getMemberId());

		// 이미 신고한 유저이며, 처리가 안됐을 경우 신고 불가
		if (reports.stream().anyMatch(report -> !report.isProcessed())) {
			throw new CommonException(ErrorCode.DUPLICATE_REPORT_NOT_ALLOWED);
		}

		Report report = Report.createReport(reportRequestDto, member, reported);
		reportRepository.save(report);
	}

	// 내 신고 내역 불러오기
	@Transactional(readOnly = true)
	public List<ReportMemberListResponseDto> getMyReports(UserDetails userDetails) {

		List<Report> myReports = reportRepository.findAllByReporterMemberId(
			securityUtils.getMember(userDetails).getMemberId());
		return myReports.stream()
			.map(ReportMemberListResponseDto::createReportResponseDto)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ReportAdminResponseDto> getAllReportsByAdmin() {

		List<Report> myReports = reportRepository.findAll();
		return myReports.stream()
			.map(ReportAdminResponseDto::createReportAdminResponseDto
			)
			.toList();
	}

}
