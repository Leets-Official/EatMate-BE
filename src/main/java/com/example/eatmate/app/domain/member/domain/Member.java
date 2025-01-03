package com.example.eatmate.app.domain.member.domain;

import static com.example.eatmate.app.domain.member.domain.Role.*;

import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
	//OAuth로 받을 수 있는거만 false 해놓기
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	@Column(nullable = true)
	private String email;

	@Column(nullable = true)
	private String name;

	@Column(nullable = true)
	private String nickname;

	@Column(nullable = true, name = "student_number")
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

	@Column(name = "refresh_token")
	private String refreshToken;

	@Embedded
	private BirthDate birthDate;   // yyyy-mm-dd 형식으로 받아야함

	@Builder
	private Member(Long memberId, String email, String name, String nickname, Long studentNumber, Mbti mbti,
		String phoneNumber, BirthDate birthDate, Boolean isActive, Gender gender, Role role, String refreshToken) {
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

	private static Member create(String email, String nickname, Mbti mbti, String phoneNumber, int year, int month,
		int day, Gender gender, Long studentNumber) {
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
			.role(USER) // 기본 역할 설정
			.build();
	}

	public static Member createDevMember(String email, String nickname, String name, Gender gender) {
		return Member.builder()
			.email(email)
			.name(name)
			.gender(gender)
			.nickname(nickname)
			.build();
	}

	public void updateMemberDetails(String nickname, String phoneNumber, Long studentNumber, Gender
		gender, BirthDate birthDate, Mbti mbti) {
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.studentNumber = studentNumber;
		this.gender = gender;
		this.role = USER;
		this.birthDate = birthDate;
		this.isActive = true;
		this.mbti = mbti;
	}

	public void updateRefreshToken(String updateRefreshToken) {
		this.refreshToken = updateRefreshToken;
	}
	//
	//
	//    public void updateNickname(String nickname) {
	//        this.nickname = nickname;
	//    }
	//
	//
	//    public void updatePhoneNumber(String phoneNumber) {
	//        this.phoneNumber = phoneNumber;
	//    }
	//
	//    public void updateStudentNumber(Long studentNumber) {
	//        this.studentNumber = studentNumber;
	//    }
	//
	//    public void updateGender(Gender gender) {
	//        this.gender = gender;
	//    }
	//
	//    public void updateBirthDate(BirthDate birthDate) {
	//        this.birthDate = birthDate;
	//    }
	//
	//    public void updateMbti(Mbti mbti) {
	//        this.mbti = mbti;
	//    }
	//
	//    public void activate() {
	//        this.isActive = true;
	//        this.role = Role.USER;
	//    }

}
