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
            setTokensInHeader(response, accessToken, refreshToken, userRole.name());
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: {} " , e.getMessage());
            throw e;
        }
    }


    private void setTokensInHeader(HttpServletResponse response, String accessToken, String refreshToken, String role) {
        response.setHeader("Authorization", "Bearer " + accessToken);
        if (refreshToken != null) {
            response.setHeader("Authorization-Refresh", "Bearer " + refreshToken);
        }
        response.setHeader("Role", role);  //헤더에 role도 담기
        log.info("헤더에 AccessToken, RefreshToken, Role 설정 완료");
    }

    // 로그용 (삭제해도 ok)
    private void logTokens(String accessToken, String refreshToken) {
        log.info("AccessToken: {}", accessToken);
        if (refreshToken != null) {
            log.info("RefreshToken: {}", refreshToken);
        }
    }
}

