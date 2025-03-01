package com.example.eatmate.global.auth.login.oauth;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.Role;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.auth.jwt.JwtService;
import com.example.eatmate.global.auth.login.dto.OAuthToken;
import com.example.eatmate.global.auth.login.dto.UserLoginResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

	private static final String GOOGLE_TOKEN_URI = "https://oauth2.googleapis.com/token";
	private static final String GOOGLE_USER_INFO_URI = "https://www.googleapis.com/oauth2/v2/userinfo";

	private final RestClient restClient = RestClient.create();
	private final MemberRepository memberRepository;
	private final JwtService jwtService;
	private final GoogleOAuth2Properties googleOAuth2Properties; // 주입받음

	public OAuthToken getGoogleAccessToken(String authCode) {
		String decodedCode = URLDecoder.decode(authCode, StandardCharsets.UTF_8);

		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
		bodyParams.add("code", decodedCode);
		bodyParams.add("client_id", googleOAuth2Properties.getClientId());
		bodyParams.add("client_secret", googleOAuth2Properties.getClientSecret());
		bodyParams.add("redirect_uri", googleOAuth2Properties.getRedirectUri());
		bodyParams.add("grant_type", "authorization_code");

		log.info(" [Google OAuth 요청] code={}, client_id={}, client_secret={}, redirect_uri={}, grant_type={}",
			decodedCode, googleOAuth2Properties.getClientId(),
			googleOAuth2Properties.getClientSecret(),
			googleOAuth2Properties.getRedirectUri(), "authorization_code");

		try {
			String responseBody = restClient.post()
				.uri(GOOGLE_TOKEN_URI)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(bodyParams)
				.retrieve()
				.body(String.class);

			log.info(" [Google OAuth 원본 응답] {}", responseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			OAuthToken tokenResponse = objectMapper.readValue(responseBody, OAuthToken.class);

			if (tokenResponse == null || tokenResponse.getAccessToken() == null || tokenResponse.getAccessToken()
				.isEmpty()) {
				log.error(" [Google OAuth 오류] Access Token이 응답에서 비어 있음");
				throw new RuntimeException("Google Access Token 발급 실패");
			}
			return tokenResponse;
		} catch (Exception e) {
			log.error(" [Google OAuth 요청 실패] 메시지: {}", e.getMessage(), e);
			throw new RuntimeException("Google Access Token 발급 실패", e);
		}
	}

	public GoogleOAuthUserInfo getGoogleUserInfo(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		return restClient.get()
			.uri(GOOGLE_USER_INFO_URI)
			.headers(httpHeaders -> httpHeaders.addAll(headers))
			.retrieve()
			.body(GoogleOAuthUserInfo.class);
	}

	public UserLoginResponseDto processGoogleUserLogin(String accessToken) {
		GoogleOAuthUserInfo googleUserInfo = getGoogleUserInfo(accessToken);
		log.info("Google 사용자 정보: {}", googleUserInfo.getAttributes());

		Member member = memberRepository.findByEmail(googleUserInfo.getEmail())
			.orElseGet(() -> {
				Member newMember = Member.builder()
					.email(googleUserInfo.getEmail())
					.name(googleUserInfo.getName())
					.role(Role.GUEST)
					.build();
				return memberRepository.save(newMember);
			});

		String jwtAccessToken = jwtService.createAccessToken(member.getEmail(), member.getRole().name(), null);
		String refreshToken = jwtService.createRefreshToken();

		member.updateRefreshToken(refreshToken);
		memberRepository.save(member);

		log.info("JWT 발급 완료: AccessToken={}, RefreshToken={}", jwtAccessToken, refreshToken);

		return new UserLoginResponseDto(
			member.getEmail(),
			member.getRole(),
			member.getGender(),
			jwtAccessToken,
			refreshToken
		);
	}
}
