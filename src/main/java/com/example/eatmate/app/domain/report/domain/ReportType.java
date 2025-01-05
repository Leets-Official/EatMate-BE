package com.example.eatmate.app.domain.report.domain;

import lombok.Getter;

@Getter
public enum ReportType {
    SPAM("스팸"),
    HARASSMENT("괴롭힘"),
    INAPPROPRIATE_CONTENT("부적절한 내용");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }
}
