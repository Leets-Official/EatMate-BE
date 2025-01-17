package com.example.eatmate.app.domain.member.dto;

import com.example.eatmate.app.domain.member.domain.BirthDate;
import com.example.eatmate.app.domain.member.domain.Mbti;
import com.example.eatmate.app.domain.member.domain.Member;

import lombok.Builder;
import lombok.Getter;

// 마이페이지 클릭 시 반환할 내 정보들(or 수정)
// 구글이메일, 닉네임, 학번, 전화번호, MBTI, 생년월일 , 프로필사진정보(추후구현)
@Getter
public class MyInfoResponseDto {
	private String email;
	private String nickname;
	private Long studentNumber;
	private Mbti mbti;
	private String phoneNumber;
	private BirthDate birthDate;
	private String profileImageUrl;

	@Builder
	private MyInfoResponseDto(String email, String nickname, Long studentNumber, Mbti mbti, String phoneNumber,
		BirthDate birthDate, String profileImageUrl) {
		this.email = email;
		this.nickname = nickname;
		this.studentNumber = studentNumber;
		this.mbti = mbti;
		this.phoneNumber = phoneNumber;
		this.birthDate = birthDate;
		this.profileImageUrl = profileImageUrl;
	}

	public static MyInfoResponseDto from(Member member) {
		return MyInfoResponseDto.builder()
			.email(member.getEmail())
			.nickname(member.getNickname())
			.studentNumber(member.getStudentNumber())
			.mbti(member.getMbti())
			.birthDate(member.getBirthDate())
			.phoneNumber(member.getPhoneNumber())
			.profileImageUrl(member.getProfileImage().getImageUrl())
			.build();
	}
}
