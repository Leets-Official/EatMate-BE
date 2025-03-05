package com.example.eatmate.global.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.eatmate.global.auth.jwt.JwtAuthenticationProcessingFilter;
import com.example.eatmate.global.auth.login.oauth.OAuthLoginFailureHandler;
import com.example.eatmate.global.auth.login.oauth.OAuthLoginSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
	private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
	private final JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ CORS 설정 추가
			.headers(headersConfigurer -> headersConfigurer.frameOptions(
				HeadersConfigurer.FrameOptionsConfig::sameOrigin))
			.sessionManagement(
				session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ✅ 세션을 사용하지 않음 (JWT 기반)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.requestMatchers("/login/oauth2/code/google").permitAll()
				.requestMatchers("/api/auth/**").permitAll() // ✅ OAuth 로그인 엔드포인트는 인증 필요 없음
				.requestMatchers("/ws/chat/**").permitAll() // ✅ WebSocket 연결 허용
				.anyRequest().authenticated() // ✅ 모든 요청은 인증 필요
			)
			.exceptionHandling(exceptionHandling ->
				exceptionHandling
					.authenticationEntryPoint((request, response, authException) -> {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
					})
					.accessDeniedHandler((request, response, accessDeniedException) -> {
						response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
					})
			)
			.addFilterBefore(jwtAuthenticationProcessingFilter,
				UsernamePasswordAuthenticationFilter.class)  // ✅ JWT 필터 추가
			.oauth2Login(oauth2 -> oauth2
				.successHandler(oAuthLoginSuccessHandler) // ✅ OAuth2 로그인 성공 시 JWT 발급
				.failureHandler(oAuthLoginFailureHandler)
			);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList(
			"http://localhost:3000",
			"https://develop.d4u0qurydeei4.amplifyapp.com",
			"https://www.eatmate.site",
			"https://eatmate.site"
		));
		configuration.addAllowedOriginPattern("*"); // 모든 도메인 허용 (필요하면 제거)

		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS", "PUT"));
		configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization")); // "Cookie" 제거

		configuration.setExposedHeaders(Arrays.asList("Authorization")); // JWT가 담긴 Authorization 헤더를 노출

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
