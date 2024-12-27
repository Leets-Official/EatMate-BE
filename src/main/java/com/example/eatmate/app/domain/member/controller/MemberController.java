package com.example.eatmate.app.domain.member.controller;

import com.example.eatmate.app.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class MemberController {

    //추후 구현 예정
    private final MemberService memberService;

}
