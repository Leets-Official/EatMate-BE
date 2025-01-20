package com.example.eatmate.global.auth.jwt;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.config.error.exception.custom.UserNotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private static final String EMAIL_CLAIM = "email";
	private static final String ROLE_CLAIM = "role";
	private final MemberRepository memberRepository;
	@Value("${jwt.secretKey}")
	private String secretKey;
	@Value("${jwt.access.expiration}")
	private Long accessTokenExpirationPeriod;
	@Value("${jwt.refresh.expiration}")
	private Long refreshTokenExpirationPeriod;

	/**
	 * 토큰 생성 메서드
	 */
	private String createToken(String subject, long expirationTime, String email, String role, String gender) {
		Date now = new Date();
		var jwtBuilder = JWT.create()
			.withSubject(subject)
			.withExpiresAt(new Date(now.getTime() + expirationTime))
			.withClaim(EMAIL_CLAIM, email);

		if (role != null) {
			jwtBuilder.withClaim(ROLE_CLAIM, role); // Role 클레임 추가
		}

		// Role이 USER인 경우 Gender를 포함
		if ("USER".equals(role) && gender != null) {
			jwtBuilder.withClaim("gender", gender); // Gender 클레임 추가
		}

		return jwtBuilder.sign(Algorithm.HMAC512(secretKey));
	}

	/**
	 * Access Token 생성 (Role 포함)
	 */
	public String createAccessToken(String email, String role, String gender) {
		return createToken(ACCESS_TOKEN_SUBJECT, accessTokenExpirationPeriod, email, role, gender);
	}

	/**
	 * Refresh Token 생성 (Role 정보 없음)
	 */
	public String createRefreshToken() {
		return createToken(REFRESH_TOKEN_SUBJECT, refreshTokenExpirationPeriod, null, null, null);
	}

	/**
	 * 공통: 쿠키에서 토큰 추출
	 */
	private Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
		return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
			.filter(cookie -> cookie.getName().equals(cookieName))
			.map(Cookie::getValue)
			.findFirst();
	}

	/**
	 * 쿠키에서 Access Token 추출
	 */
	public Optional<String> extractAccessTokenFromCookie(HttpServletRequest request) {
		return extractTokenFromCookie(request, "AccessToken");
	}

	/**
	 * 쿠키에서 Refresh Token 추출
	 */
	public Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
		return extractTokenFromCookie(request, "RefreshToken");
	}

	/**
	 * Access Token에서 Email 추출
	 */
	public Optional<String> extractEmail(String accessToken) {
		return extractClaim(accessToken, EMAIL_CLAIM);
	}

	/**
	 * Access Token에서 Role 추출
	 */
	public Optional<String> extractRole(String accessToken) {
		return extractClaim(accessToken, ROLE_CLAIM);
	}

	/**
	 * Access Token에서 Gender 추출
	 */
	public Optional<String> extractGender(String accessToken) {
		return extractClaim(accessToken, "gender");
	}

	/**
	 * 공통: 토큰에서 Claim 추출
	 */
	private Optional<String> extractClaim(String token, String claim) {
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey)).build();
			return Optional.ofNullable(verifier.verify(token).getClaim(claim).asString());
		} catch (Exception e) {
			log.error("토큰에서 {} 추출 실패: {}", claim, e.getMessage());
			return Optional.empty();
		}
	}

	/**
	 * 토큰 유효성 검증
	 */
	public boolean isTokenValid(String token) {
		try {
			JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
			return true;
		} catch (TokenExpiredException e) {
			log.warn("토큰 만료: {}", e.getMessage());
		} catch (SignatureVerificationException e) {
			log.warn("서명 검증 실패: {}", e.getMessage());
		} catch (JWTDecodeException e) {
			log.warn("토큰 디코딩 실패: {}", e.getMessage());
		} catch (Exception e) {
			log.error("알 수 없는 토큰 검증 오류: {}", e.getMessage());
		}
		return false;
	}

	/**
	 * Refresh Token 업데이트
	 */
	@Transactional
	public void updateRefreshToken(String email, String refreshToken) {
		memberRepository.findByEmail(email).ifPresentOrElse(
			member -> {
				member.updateRefreshToken(refreshToken);
				memberRepository.saveAndFlush(member);
			},
			() -> {
				throw new UserNotFoundException();
			});
	}
}
