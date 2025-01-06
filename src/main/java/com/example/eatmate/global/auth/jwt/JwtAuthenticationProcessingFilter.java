package com.example.eatmate.global.auth.jwt;


import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.rmi.server.ServerCloneException;

/**
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 *
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login"; // 로그인 요청은 필터 제외

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response); // /login 요청은 필터 제외
            return;
        }

        // Refresh Token 처리
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (refreshToken != null) {
            handleRefreshToken(response, refreshToken);
            return; // 토큰 재발급 후 필터 종료
        }

        // Access Token 처리
        handleAccessToken(request, response, filterChain);
    }

    /**
     * Refresh Token 처리 및 Access Token 재발급
     */
    private void handleRefreshToken(HttpServletResponse response, String refreshToken) {
        memberRepository.findByRefreshToken(refreshToken)
                .ifPresentOrElse(
                        member -> {
                            String newAccessToken = jwtService.createAccessToken(member.getEmail(), member.getRole().name());
                            String newRefreshToken = jwtService.createRefreshToken();
                            member.updateRefreshToken(newRefreshToken);
                            memberRepository.saveAndFlush(member);

                            // 헤더에 토큰 설정
                            response.setHeader("Authorization", "Bearer " + newAccessToken);
                            response.setHeader("Authorization-Refresh", "Bearer " + newRefreshToken);
                            log.info("Refresh Token 유효. Access Token 및 Refresh Token 재발급 완료.");
                        },
                        () -> sendErrorResponse(response, "Invalid Refresh Token")
                );
    }

    /**
     * Access Token 처리 및 SecurityContext 설정
     */
    private void handleAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .flatMap(jwtService::extractEmail)
                .flatMap(memberRepository::findByEmail)
                .ifPresent(this::setAuthentication);

        filterChain.doFilter(request, response);
    }

    /**
     * 인증 정보 SecurityContext에 저장
     */
    private void setAuthentication(Member member) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(member.getEmail())
                .password("") // 비밀번호는 사용하지 않으므로 빈 문자열
                .roles(member.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 에러 응답 전송
     */
    private void sendErrorResponse(HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("Error-Message", message); // 에러 메시지를 헤더에 설정
            log.warn("에러 응답: {}", message);
        } catch (Exception e) {
            log.error("에러 응답 전송 중 오류 발생", e);
        }
    }
}