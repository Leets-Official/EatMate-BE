package com.example.eatmate.app.domain.member.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class BirthDate {

    @NotNull
    @Column(length = 4)
    private int year;

    @NotNull
    @Column(length = 2)
    private int month;

    @NotNull
    @Column(length = 2)
    private int day;

    @Builder
    private BirthDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    // 정적 팩토리 메서드
    public static BirthDate of(int year, int month, int day) {
        return new BirthDate(year, month, day);
    }
}
