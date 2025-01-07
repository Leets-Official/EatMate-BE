package com.example.eatmate.global.auth.login.oauth;

import com.example.eatmate.app.domain.member.domain.Role;
import com.example.eatmate.global.auth.jwt.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공");

        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // 사용자 Role 확인
            Role userRole = oAuth2User.getRole();
            log.info("사용자 Role : {}" , userRole);

            //토큰 생성
            String accessToken = jwtService.createAccessToken(oAuth2User.getEmail(), oAuth2User.getRole().name() );
            String refreshToken = null;

            if (userRole == Role.USER) {
                refreshToken = jwtService.createRefreshToken();
                jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
            }
            logTokens(accessToken, refreshToken);

            setTokensInCookie(response,accessToken, refreshToken);

			response.sendRedirect("http://localhost:3000/oauth2/callback");
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: {} " , e.getMessage());
            throw e;
        }
    }

// 쿠키 설정 메소드 생성
    private void setTokensInCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        // Access Token 쿠키 설정
        Cookie accessTokenCookie = new Cookie("AccessToken", accessToken);
        accessTokenCookie.setHttpOnly(true); // 자바스크립트에서 접근 불가 (보안 강화)
        accessTokenCookie.setSecure(false); // HTTPS 에서만 전송
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간

        // SameSite 설정
        response.addHeader("Set-Cookie", "AccessToken=" + accessToken +
                "; HttpOnly; Secure=true; SameSite=None; Path=/; Max-Age=" + (60 * 60));

        // Refresh Token 쿠키 설정 (필요 시)
        Cookie refreshTokenCookie = null;
        if (refreshToken != null) {
            refreshTokenCookie = new Cookie("RefreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일

            // SameSite 설정
            response.addHeader("Set-Cookie", "RefreshToken=" + refreshToken +
                    "; HttpOnly; Secure=true; SameSite=None; Path=/; Max-Age=" + (60 * 60 * 24 * 7));
        }


        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);
        if (refreshTokenCookie != null) {
            response.addCookie(refreshTokenCookie);
        }

        log.info("쿠키에 AccessToken, RefreshToken 설정 완료");
    }


    // 로그용 (삭제해도 ok)
    private void logTokens(String accessToken, String refreshToken) {
        log.info("AccessToken: {}", accessToken);
        if (refreshToken != null) {
            log.info("RefreshToken: {}", refreshToken);
        }
    }
}

