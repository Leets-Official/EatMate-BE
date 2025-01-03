package com.example.eatmate.global.auth.login.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.eatmate.app.domain.member.domain.Member;

public class CustomUserDetails implements UserDetails {
	private final Member member;

	public CustomUserDetails(Member member) {
		this.member = member;
	}

	public Long getMemberId() {
		return member.getMemberId();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public String getUsername() {
		return "";
	}
}
