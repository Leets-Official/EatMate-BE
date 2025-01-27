package com.example.eatmate.app.domain.report.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.report.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
	List<Report> findAllByReporterMemberId(Long memberId);

	List<Report> findAllByReporterMemberIdAndReportedMemberId(Long reporterId, Long reportedId);
}
