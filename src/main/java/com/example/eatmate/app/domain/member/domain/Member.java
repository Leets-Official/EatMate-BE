package com.example.eatmate.app.domain.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
public class Member {
//OAuth로 받을 수 있는거만 false 해놓기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column (nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String studentNumber;

    @Column(nullable = true)
    private String mbti;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private int birthYear;

    @Column(nullable = false)
    private Boolean isActive = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String refreshToken;

    public Member(Long memberId, String email, String name, String nickname, String studentNumber, String mbti, String phoneNumber, int birthYear, Boolean isActive, Gender gender, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.studentNumber = studentNumber;
        this.mbti = mbti;
        this.phoneNumber = phoneNumber;
        this.birthYear = birthYear;
        this.isActive = isActive;
        this.gender = gender;
        this.role = role;
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}
