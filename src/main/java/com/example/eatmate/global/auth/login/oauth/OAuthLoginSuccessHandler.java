package com.example.eatmate.global.auth.login.oauth;


import com.example.eatmate.app.domain.member.domain.Role;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.auth.jwt.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
// OAuth2 로그인 성공시 , 실행되는 로직
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
            //처음 로그인(회원가입일 경우) 회원가입 페이지로 리다이렉트
            if (oAuth2User.getRole() == Role.GUEST) {
                String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
                response.sendRedirect("/"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트

                jwtService.sendAccessAndRefreshToken(response, accessToken, null);
            } else {
                loginSuccess(response, oAuth2User); // 로그인 성공시  Access & Refresh 토큰 생성

            }
        } catch (Exception e) {
            throw e;
        }
    }
        private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
            String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
            log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
            String refreshToken = jwtService.createRefreshToken();
            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
            response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

            jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
            jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
        }

    }

