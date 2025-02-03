package com.example.eatmate.app.domain.report.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

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

	@JsonCreator
	public static ReportType parsing(String inputValue) {
		return Stream.of(ReportType.values())
			.filter(category -> category.toString().equals(inputValue.toUpperCase()))
			.findFirst()
			.orElse(null);
	}
}
