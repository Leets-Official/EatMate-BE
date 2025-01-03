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
            if (oAuth2User.getRole() == Role.GUEST) {
                log.info("회원가입이 필요한 사용자입니다. 회원가입 페이지로 이동.");
                handleGuestUser(response, oAuth2User);
            } else {
                log.info("회원가입이 완료된 사용자입니다. 메인 페이지로 이동.");
                handleRegisteredUser(response, oAuth2User);
            }

        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    private void handleGuestUser(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        log.info("auth header : " + response.getHeader(jwtService.getAccessHeader()));


        // GUEST 사용자는 회원가입 페이지로 리다이렉트
        jwtService.sendAccessAndRefreshToken(response, accessToken, null);

        response.sendRedirect("https://develop.d4u0qurydeei4.amplifyapp.com/signup");
        //response.sendRedirect("http://localhost:8080");
    }

    private void handleRegisteredUser(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        log.info("auth access header : " + response.getHeader(jwtService.getAccessHeader()));
        log.info("auth refresh header : " + response.getHeader(jwtService.getRefreshHeader()));


        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);



        // USER 사용자는 메인 페이지로 리다이렉트
        response.sendRedirect("https://develop.d4u0qurydeei4.amplifyapp.com/home");
        // response.sendRedirect("http://localhost:8080");
    }
}
