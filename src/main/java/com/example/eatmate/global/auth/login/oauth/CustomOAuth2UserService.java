package com.example.eatmate.global.auth.login.oauth;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.Role;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//Spring Security가 OAuth 인증 후 사용자 정보를 가져와 처리하는 커스텀 서비스.

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

		// 사용자 정보 로드
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		// 사용자 정보 추출
		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
		Map<String, Object> attributes = oAuth2User.getAttributes();

		// 사용자 정보를 매핑하여 추출
		OAuthAttributes extractAttributes = OAuthAttributes.of(userNameAttributeName, attributes);

		// 이메일 도메인 필터링
		validateEmailDomain(extractAttributes.getEmail());

		// 사용자 정보 조회 또는 저장
		Member member = getMember(extractAttributes);

		// 사용자 정보를 CustomOAuth2User로 반환
		return new CustomOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(member.getRole() != null
				? member.getRole().getRoleType() : Role.GUEST.getRoleType())), // 권한 설정
			attributes, // 사용자 속성
			extractAttributes.getNameAttributeKey(), // 기본 식별자 키
			member.getRole() != null ? member.getRole() : Role.GUEST // Role 전달
		);

	}

	// 가천 도메인 필터링
	private void validateEmailDomain(String email) {
		if (!email.endsWith("@gachon.ac.kr")) {
			throw new IllegalArgumentException("가천대학교 학생만 이용가능합니다.");
		}
	}

	//사용자 정보를 조회하거나 새로 저장
	private Member getMember(OAuthAttributes attributes) {
		return memberRepository.findByEmail(attributes.getEmail())
			.orElseGet(() -> saveMember(attributes));
	}

	// 신규 사용자 저장
	private Member saveMember(OAuthAttributes attributes) {
		Member createdMember = attributes.toEntity();
		return memberRepository.save(createdMember);
	}
}
