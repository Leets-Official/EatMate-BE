package com.example.eatmate.app.domain.member.service;


import com.example.eatmate.app.domain.member.domain.BirthDate;
import com.example.eatmate.app.domain.member.domain.Mbti;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.app.domain.member.dto.MemberSignUpRequestDto;
import com.example.eatmate.global.auth.jwt.JwtService;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;
import com.example.eatmate.global.config.error.exception.custom.MemberAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    // 회원 가입 완료시 , 기본 값에서 사용자 입력값으로 업데이트 해주는 메소드
    public Void completeRegistration(MemberSignUpRequestDto signUpRequestDto, UserDetails userDetails) {
        String email = userDetails.getUsername();

        validateSignUpData(signUpRequestDto);
        // 이메일로 기존 사용자 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));


        member.updateMemberDetails(signUpRequestDto.getNickname(), signUpRequestDto.getPhoneNumber(), signUpRequestDto.getStudentNumber(), signUpRequestDto.getGender(), BirthDate.of(signUpRequestDto.getYear(), signUpRequestDto.getMonth(), signUpRequestDto.getDay()), signUpRequestDto.getMbti());
        return null;
    }

    private void validateSignUpData(MemberSignUpRequestDto signUpRequestDto) {
        // 성별 필수 선택 검증
        if (signUpRequestDto.getGender() == null) {
            throw new CommonException(ErrorCode.INVALID_GENDER);
        }

        // 전화번호 유효성 검증
        if (!signUpRequestDto.getPhoneNumber().matches("^\\d{10,11}$")) {
            throw new CommonException(ErrorCode.INVALID_PHONE_NUMBER);
        }
        if (memberRepository.existsByPhoneNumber(signUpRequestDto.getPhoneNumber())) {
            throw new CommonException(ErrorCode.DUPLICATE_PHONE_NUMBER); // 전화번호 중복 예외 발생
        }

        // 학번 유효성 검증
        if (signUpRequestDto.getStudentNumber() == null || String.valueOf(signUpRequestDto.getStudentNumber()).length() != 9) {
            throw new CommonException(ErrorCode.INVALID_STUDENT_NUMBER);
        }
        if (memberRepository.existsByStudentNumber(signUpRequestDto.getStudentNumber())) {
            throw new CommonException(ErrorCode.DUPLICATE_STUDENT_NUMBER); // 학번 중복 예외 발생
        }


        // 닉네임 중복 확인
        if (memberRepository.existsByNickname(signUpRequestDto.getNickname())) {
            throw new CommonException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // MBTI 검증
        Mbti.fromString(signUpRequestDto.getMbti().name()); // fromString에서 검증 처리
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


}

