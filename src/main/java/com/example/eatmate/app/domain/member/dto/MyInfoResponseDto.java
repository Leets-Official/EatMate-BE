package com.example.eatmate.app.domain.member.dto;

import com.example.eatmate.app.domain.member.domain.BirthDate;
import com.example.eatmate.app.domain.member.domain.Mbti;
import com.example.eatmate.app.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//마이페이지 클릭 시 반환할 내 정보들(or 수정)
//구글이메일, 닉네임, 학번, 전화번호, MBTI, 생년월일 , 프로필사진정보(추후구현)
@Getter
@Builder
@AllArgsConstructor
public class MyInfoResponseDto {
    private String email;
    private String nickname;
    private Long studentNumber;
    private Mbti mbti;
    private String phoneNumber;
    private BirthDate birthDate;

    public static MyInfoResponseDto of(Member member) {
        return MyInfoResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .studentNumber(member.getStudentNumber())
                .mbti(member.getMbti())
                .birthDate(member.getBirthDate())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }



}
