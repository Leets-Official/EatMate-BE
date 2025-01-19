package com.example.eatmate.global.common.util;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityUtils {
	private final MemberRepository memberRepository;

	public Member getMember(UserDetails userDetails) {
		if (userDetails == null) {
			throw new CommonException(ErrorCode.INVALID_LOGIN_INFO);
		}
		return memberRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));
	}
}
