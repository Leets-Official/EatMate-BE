package com.example.eatmate.app.domain.member.service;


import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.app.domain.member.dto.MemberSignUpRequestDto;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.custom.MemberAlreadyExistsException;
import com.example.eatmate.global.config.error.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입 완료시 , 기본 값에서 사용자 입력값으로 업데이트 해주는 메소드
    public void completeRegistration(MemberSignUpRequestDto signUpRequestDto) {
        // 이메일로 기존 사용자 조회
        Member member = memberRepository.findByEmail(signUpRequestDto.getEmail())
                .orElseThrow(UserNotFoundException::new);
        // 추가 정보 업데이트
        updateMemberDetails(member, signUpRequestDto);
        // 변경된 정보 저장
        memberRepository.save(member);
    }


    public String register(MemberSignUpRequestDto memberSignUpRequestDto) {
        if (memberRepository.findByEmail(memberSignUpRequestDto.getEmail()).isPresent()) {
            throw new MemberAlreadyExistsException();
        }

        Member member = Member.create(
                memberSignUpRequestDto.getEmail(),
                memberSignUpRequestDto.getNickname(),
                memberSignUpRequestDto.getMbti(),
                memberSignUpRequestDto.getPhoneNumber(),
                memberSignUpRequestDto.getYear(),
                memberSignUpRequestDto.getMonth(),
                memberSignUpRequestDto.getDay(),
                memberSignUpRequestDto.getGender(),
                memberSignUpRequestDto.getStudentNumber()
        );

        memberRepository.save(member);

        return "회원가입이 완료되었습니다.";
    }

    private void updateMemberDetails(Member member, MemberSignUpRequestDto signUpRequestDto) {
        member.updateNickname(signUpRequestDto.getNickname());
        member.updatePhoneNumber(signUpRequestDto.getPhoneNumber());
        member.updateStudentNumber(signUpRequestDto.getStudentNumber());
        member.updateGender(signUpRequestDto.getGender());
        member.activate();
    }


}

