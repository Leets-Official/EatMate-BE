package com.example.eatmate.app.domain.member.dto;


import com.example.eatmate.app.domain.member.domain.Gender;
import com.example.eatmate.app.domain.member.domain.Mbti;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class MemberSignUpRequestDto {
    //생년월일, 성별, 전화번호, MBTI, 학번, 닉네임, 프로필 사진 (일단 패스)
    private int year;
    private int month;
    private int day;
    private Gender gender;
    private String phoneNumber;
    private Mbti mbti;
    private Long studentNumber;
    private String nickname;


}
