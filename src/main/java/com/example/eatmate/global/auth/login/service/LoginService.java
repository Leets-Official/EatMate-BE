package com.example.eatmate.global.auth.login.service;

import static org.springframework.security.core.userdetails.User.*;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.config.error.exception.custom.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);


        return builder()
                .username(member.getEmail())
                .build();
    }
}
