package com.example.eatmate.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.eatmate.global.auth.jwt.JwtHandshakeInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${socket.port}")
	private String port;

	@Value("${spring.rabbitmq.username}")
	private String userName;

	@Value("${spring.rabbitmq.password}")
	private String userPw;

	private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
			.setRelayHost(host)
			.setRelayPort(Integer.parseInt(port))
			.setClientLogin(userName)
			.setClientPasscode(userPw);

		registry.setPathMatcher(new AntPathMatcher("."));
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override //소켓연결 엔드포인트 경로
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws/chat")
			.setAllowedOriginPatterns("*");
			//.addInterceptors(jwtHandshakeInterceptor);
			//.withSockJS();
	}
}
