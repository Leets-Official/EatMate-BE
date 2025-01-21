package com.example.eatmate.app.domain.report.domain;

import lombok.Getter;

@Getter
public enum ReportType {
	OFFENSIVE("욕설"),
	HARASSMENT("성희롱"),
	OTHER("기타");

	private final String description;

	ReportType(String description) {
		this.description = description;
	}
}
