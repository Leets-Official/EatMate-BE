package com.example.eatmate.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class EnvVariableTestConfig {

	@Value("${GOOGLE_CLIENT_ID}")
	private String clientId;

	@Value("${GOOGLE_CLIENT_SECRET}")
	private String clientSecret;

	@Value("${GOOGLE_REDIRECT_URI}")
	private String redirectUri;

	@PostConstruct
	public void logEnvVariables() {
		log.info("====== Environment Variables Test ======");
		log.info("GOOGLE_CLIENT_ID    = {}", clientId);
		log.info("GOOGLE_CLIENT_SECRET= {}", clientSecret);
		log.info("GOOGLE_REDIRECT_URI = {}", redirectUri);
		log.info("========================================");
	}
}