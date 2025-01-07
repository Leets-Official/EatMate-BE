package com.example.eatmate.global.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
			.cors(Customizer.withDefaults())
			.headers(
				headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(
				authorize -> authorize
					.requestMatchers("/api/admin/**").hasRole("ADMIN")
					// 아이콘, css, js 관련
					// 기본 페이지, css, image, js 하위 폴더에 있는 자료들은 모두 접근 가능, h2-console에 접근 가능
					// 			.requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico").permitAll()
					// 			.requestMatchers("/v3/api-docs", "/v3/api-docs/", "/swagger-ui.html", "/swagger-ui/", "/swagger/**").permitAll()
					// 			.requestMatchers("/register").permitAll()
					.anyRequest().permitAll()
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
			.addFilterBefore(jwtAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)  // 필터 순서 확인
			//== 소셜 로그인 설정 ==//
			.oauth2Login(oauth2 -> oauth2.successHandler(oAuthLoginSuccessHandler)
				.failureHandler(oAuthLoginFailureHandler)); // 소셜 로그인 실패 시 핸들러 설정
		//.userInfoEndpoint().userService(customOAuth2UserService)); // customUserService 설정

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList(
				"http://localhost:3000",
				"https://develop.d4u0qurydeei4.amplifyapp.com"
		));
		configuration.setAllowedOrigins(Arrays.asList("https://develop.d4u0qurydeei4.amplifyapp.com"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("Authorization", "Authorization_refresh", "accept"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
