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
 * 사용자는 요청 헤더에 AccessToken만 담아서 요청하나, 만료시에만 RefreshToken을 요청 헤더에 담아서 함께 요청
 *
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login"; //로그인으로 들어오는 요청은 필터 작동 x

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request,response); // 로그인 요청들어오면 다음의 필터 호출
            return; // 현재 필터 진행 막기
        }

        //요청 헤더에서 재발급 토큰 추출하고 유효하지 않거나, 나머지 경우는 null 반환
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);


        if(refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return; // 재발급 토큰을 보낸 후에는 , Access 토큰 재발급하고 인증처리는 안가게끔 막기
        }

        if(refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
            // 토큰이 유효하다면 다음 필터로, 아니라면 403에러 발생
        }

        // http 요청 -> (/api/v1/members/login)엔드포인트에 맞는 컨트롤러
        // http 요청 -> JwtAuthenticationProcessingFilter (요기서 jwt검증, 파싱 유저를 생성) 가로챔 -> 컨트롤러
    }

    /**
     * 리프레시 토큰으로 유저 정보 찾기 & 엑세스 토큰/리프레시 토큰 재발급 메소드
     * 1. 헤더에서 추출한 리프레시 토큰으로 DB 유저찾기
     * 2. 유저가 있다면 AccessToken 와 리프레시 토큰 재발급 & DB에 토큰 업데이트
     * 3. 응답 헤더에 담아 보내기
     */

    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        memberRepository.findByRefreshToken(refreshToken)
                .ifPresent(member -> {
                    String reIssuedRefreshToken = reIssueRefreshToken(member);
                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(member.getEmail()) ,
                            reIssuedRefreshToken);
                });
    }

    /**
     * 리프레시 토큰 & DB에 리프레시 토큰 업데이트
     */

    private String reIssueRefreshToken(Member member) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        member.updateRefreshToken(reIssuedRefreshToken);
        memberRepository.saveAndFlush(member);
        return reIssuedRefreshToken;
    }

    /**
     * 엑세스 토큰 체크 & 인증 처리 메소드
     * request에서 엑세스 토큰 추출 후, 유효한 토큰인지 검증
     * 유효하다면, 엑세스 토큰에서 email 추출한 후 , findByEmail로 해당 이메일 사용하는 유저 객체 반환
     * 반환된 유저를 saveAuthenticaiton으로 인증 처리한 후, SecurityContextHolder에 담기
     *  그 후 다음 인증 필터로 진행
     */

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)
                        .ifPresent(email -> memberRepository.findByEmail(email)
                                .ifPresent(this::saveAuthentication)));

        filterChain.doFilter(request, response);
    }

    /**
     * [인증 허가 메소드]
     * 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetails의 User 객체
     * new UsernamePasswordAuthenticationToken()로 인증 객체인 Authentication 객체 생성
     * UsernamePasswordAuthenticationToken의 파라미터
     * 1. 위에서 만든 UserDetailsUser 객체 (유저 정보)
     * 2. credential(보통 비밀번호로, 인증 시에는 보통 null로 제거)
     * 3. Collection < ? extends GrantedAuthority>로,
     * UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에,
     * new NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     */
    public void saveAuthentication(Member myMember) {
        // OAuth2를 통해 인증된 사용자 정보로 UserDetails 객체 생성
        UserDetails userDetailsMember = org.springframework.security.core.userdetails.User.builder()
                .username(myMember.getEmail())
                .password("") // 비밀번호는 사용하지 않으므로 빈 문자열
                .roles(myMember.getRole().name()) // 역할은 사용자의 Role에 따라 설정
                .build();

        // Authentication 객체 생성 및 권한 매핑
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsMember, null,
                        authoritiesMapper.mapAuthorities(userDetailsMember.getAuthorities()));

        // SecurityContext에 Authentication 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
