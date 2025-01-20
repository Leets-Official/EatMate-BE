package com.example.eatmate.app.domain.member.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.eatmate.app.domain.image.domain.Image;
import com.example.eatmate.app.domain.image.domain.ImageType;
import com.example.eatmate.app.domain.image.service.ImageSaveService;
import com.example.eatmate.app.domain.member.domain.BirthDate;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.app.domain.member.dto.MemberLoginRequestDto;
import com.example.eatmate.app.domain.member.dto.MemberLoginResponseDto;
import com.example.eatmate.app.domain.member.dto.MemberSignUpRequestDto;
import com.example.eatmate.app.domain.member.dto.MyInfoResponseDto;
import com.example.eatmate.app.domain.member.dto.MyInfoUpdateRequestDto;
import com.example.eatmate.app.domain.member.dto.UserInfoResponseDto;
import com.example.eatmate.global.auth.jwt.JwtService;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final JwtService jwtService;
	private final ImageSaveService imageSaveService; // ImageSaveService 주입

	public Void completeRegistration(MemberSignUpRequestDto signUpRequestDto, MultipartFile profileImage,
		UserDetails userDetails) {

		// 프로필 이미지 업로드 처리
		Image profileImageEntity = uploadProfileImage(profileImage);

		// 기존 회원가입 로직
		String email = userDetails.getUsername();
		validateSignUpData(signUpRequestDto, profileImage);

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		member.updateMemberDetails(
			signUpRequestDto.getNickname(),
			signUpRequestDto.getPhoneNumber(),
			signUpRequestDto.getStudentNumber(),
			signUpRequestDto.getGender(),
			BirthDate.of(signUpRequestDto.getYear(), signUpRequestDto.getMonth(), signUpRequestDto.getDay()),
			signUpRequestDto.getMbti(),
			profileImageEntity
		);

		return null;
	}

	private void validateSignUpData(MemberSignUpRequestDto signUpRequestDto, MultipartFile profileImage) {

		// 전화번호 중복 검증
		if (memberRepository.existsByPhoneNumber(signUpRequestDto.getPhoneNumber())) {
			throw new CommonException(ErrorCode.DUPLICATE_PHONE_NUMBER); // 전화번호 중복 예외 발생
		}

		// 학번 중복 검증
		if (memberRepository.existsByStudentNumber(signUpRequestDto.getStudentNumber())) {
			throw new CommonException(ErrorCode.DUPLICATE_STUDENT_NUMBER); // 학번 중복 예외 발생
		}

		// 닉네임 중복 확인
		if (memberRepository.existsByNickname(signUpRequestDto.getNickname())) {
			throw new CommonException(ErrorCode.DUPLICATE_NICKNAME);
		}

	}

	// 프로필 조회 메서드
	public MyInfoResponseDto getMyInfo(UserDetails userDetails) {

		String email = userDetails.getUsername();

		Member member = memberRepository.findByEmailWithProfileImage(email)
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		return MyInfoResponseDto.from(member);
	}

	// 상대방 프로필 조회 메서드
	public UserInfoResponseDto getProfileInfo(Long memberId) {
		// 1. Member 엔티티 조회
		Member member = memberRepository.findByIdWithProfileImage(memberId)
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		// 2. DTO 변환 후 반환
		return UserInfoResponseDto.from(member);
	}

	//프로필 수정 메서드
	public MyInfoResponseDto updateMyInfo(UserDetails userDetails, MyInfoUpdateRequestDto myInfoUpdateRequestDto,
		MultipartFile profileImage) {
		// 로그인한 사용자의 이메일로 Member 조회
		String email = userDetails.getUsername();
		Member member = memberRepository.findByEmailWithProfileImage(email)
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		// 닉네임 중복 확인
		if (myInfoUpdateRequestDto.getNickname() != null &&
			memberRepository.existsByNickname(myInfoUpdateRequestDto.getNickname())) {
			throw new CommonException(ErrorCode.DUPLICATE_NICKNAME);
		}

		// 닉네임 업데이트
		if (myInfoUpdateRequestDto.getNickname() != null) {
			member.updateNickname(myInfoUpdateRequestDto.getNickname());
		}

		// MBTI 업데이트
		if (myInfoUpdateRequestDto.getMbti() != null) {
			member.updateMbti(myInfoUpdateRequestDto.getMbti());
		}

		// 프로필 이미지 업로드 처리
		Image profileImageEntity = uploadProfileImage(profileImage);
		member.updateProfileImage(profileImageEntity);

		// 업데이트된 Member 정보 반환
		return MyInfoResponseDto.from(member);
	}

	// 개발용 임시 로그인/회원가입

	public MemberLoginResponseDto login(MemberLoginRequestDto memberLoginRequestDto) {

		Member member = memberRepository.findByEmail(memberLoginRequestDto.getEmail())
			.orElseGet(() -> Member.createDevMember(memberLoginRequestDto.getEmail(),
				memberLoginRequestDto.getNickname(), memberLoginRequestDto.getName(),
				memberLoginRequestDto.getGender()));

		String accessToken = jwtService.createAccessToken(member.getEmail(), member.getRole().name());
		String refreshToken = jwtService.createRefreshToken();

		member.updateRefreshToken(refreshToken);

		memberRepository.save(member);

		return MemberLoginResponseDto.of(accessToken, refreshToken);
	}

	private Image uploadProfileImage(MultipartFile profileImage) {
		if (profileImage != null && !profileImage.isEmpty()) {
			return imageSaveService.uploadImage(profileImage, ImageType.PROFILE);
		}
		return null; // 프로필 이미지가 없으면 null 반환
	}

}

//    private void updateMemberDetails(Member member, MemberSignUpRequestDto signUpRequestDto) {
//        member.updateNickname(signUpRequestDto.getNickname());
//        member.updatePhoneNumber(signUpRequestDto.getPhoneNumber());
//        member.updateStudentNumber(signUpRequestDto.getStudentNumber());
//        member.updateGender(signUpRequestDto.getGender());
//        member.updateMbti(signUpRequestDto.getMbti());
//        member.updateBirthDate(BirthDate.of(signUpRequestDto.getYear(), signUpRequestDto.getMonth(), signUpRequestDto.getDay()));
//        member.activate();
//
//    }



