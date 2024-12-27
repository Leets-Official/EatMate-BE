package com.example.eatmate.global.auth.login.controller;

import com.example.eatmate.app.domain.member.dto.MemberSignUpRequestDto;
import com.example.eatmate.app.domain.member.service.MemberService;
import com.example.eatmate.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    //회원가입
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입을 합니다.")
    public ResponseEntity<GlobalResponseDto<String>> register(@RequestBody MemberSignUpRequestDto memberSignUpRequestDto) {

        memberService.completeRegistration(memberSignUpRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponseDto.success(memberService.register(memberSignUpRequestDto) , HttpStatus.CREATED.value()));
    }



    /*//로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자의 이메일과 비밀번호로 로그인합니다.")
    public String login(@RequestBody MemberLoginDto memberLoginDto) {
        return "로그인 성공";
    }*/

    //로그아웃
}
