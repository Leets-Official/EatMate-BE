package com.example.eatmate.global.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.config.error.exception.custom.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";
    private static final String BEARER = "Bearer";

    private final MemberRepository memberRepository;

    /**
     * 토큰 생성 메서드
     */
    private String createToken(String subject, long expirationTime, String email, String role) {
        Date now = new Date();
        var jwtBuilder = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(now.getTime() + expirationTime))
                .withClaim(EMAIL_CLAIM, email);

        if (role != null) {
            jwtBuilder.withClaim(ROLE_CLAIM, role); // Role 클레임 추가
        }

        return jwtBuilder.sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * Access Token 생성 (Role 포함)
     */
    public String createAccessToken(String email, String role) {
        return createToken(ACCESS_TOKEN_SUBJECT, accessTokenExpirationPeriod, email, role);
    }

    /**
     * Refresh Token 생성 (Role 정보 없음)
     */
    public String createRefreshToken() {
        return createToken(REFRESH_TOKEN_SUBJECT, refreshTokenExpirationPeriod, null, null);
    }

    /**
     * 요청 헤더에서 Access Token 추출
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return extractToken(request, accessHeader);
    }

    /**
     * 요청 헤더에서 Refresh Token 추출
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return extractToken(request, refreshHeader);
    }

    /**
     * 공통: 헤더에서 토큰 추출
     */
    private Optional<String> extractToken(HttpServletRequest request, String header) {
        return Optional.ofNullable(request.getHeader(header))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.replace(BEARER + " ", ""));
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
                    memberRepository.save(member);
                },
                () -> {
                    throw new UserNotFoundException();
                });
    }
}
