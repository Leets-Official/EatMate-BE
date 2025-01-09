package com.example.eatmate.app.domain.member.dto;

import com.example.eatmate.app.domain.member.domain.BirthDate;
import com.example.eatmate.app.domain.member.domain.Mbti;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyInfoUpdateRequestDto {
    private String nickname;
    private Long studentNumber;
    private Mbti mbti;
    private String phoneNumber;
    private Integer year;
    private Integer month;
    private Integer day;

    // BirthDate 객체 생성 메서드
    public BirthDate toBirthDate(BirthDate currentBirthDate) {
        return BirthDate.of(
                year != null ? year : currentBirthDate.getYear(),
                month != null ? month : currentBirthDate.getMonth(),
                day != null ? day : currentBirthDate.getDay()
        );
    }
}
