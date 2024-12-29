package com.example.eatmate.app.domain.member.domain;

import com.example.eatmate.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
//OAuth로 받을 수 있는거만 false 해놓기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String name;

    @Column (nullable = true)
    private String nickname;

    @Column(nullable = true)
    private Long studentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Mbti mbti;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Role role;

    private String refreshToken;

    @Embedded
    private BirthDate birthDate;   // yyyy-mm-dd 형식으로 받아야함

    @Builder
    public Member(Long memberId, String email, String name, String nickname, Long studentNumber, Mbti mbti, String phoneNumber, BirthDate birthDate, Boolean isActive, Gender gender, Role role, String refreshToken) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.studentNumber = studentNumber;
        this.mbti = mbti;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.isActive = isActive;
        this.gender = gender;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    public static Member create(String email, String nickname, Mbti mbti, String phoneNumber, int year, int month, int day, Gender gender, Long studentNumber) {
        // BirthDate 객체 생성
        BirthDate birthDate = BirthDate.of(year, month, day);


        // Member 객체 생성
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .mbti(mbti)
                .phoneNumber(phoneNumber)
                .birthDate(birthDate) // BirthDate 설정
                .gender(gender)
                .studentNumber(studentNumber)
                .role(Role.USER) // 기본 역할 설정
                .build();
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }


    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }


    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateStudentNumber(Long studentNumber) {
        this.studentNumber = studentNumber;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void activate() {
        this.isActive = true;
    }


}
