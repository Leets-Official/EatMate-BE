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
    private Mbti mbti;


}
