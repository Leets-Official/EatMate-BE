package com.example.eatmate.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${socket.port}")
	private String port;

	@Value("${spring.rabbitmq.username}")
	private String userName;

	@Value("${spring.rabbitmq.password}")
	private String userPw;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		//queue: 1:1 개인 메시징 기능이 필요하다면 추가.
		//topic: 1:N 메시지 브로드캐스트(예: 채팅방, 알림 시스템)라면 필수.
		//exchange: RabbitMQ의 Exchange에 대한 세밀한 라우팅이 필요할 경우 추가.
		//amq/queue: 특정 Queue에 직접 접근해야 하는 경우에만 추가.
		registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
			.setRelayHost(host)
			.setRelayPort(Integer.parseInt(port))
			.setClientLogin(userName)
			.setClientPasscode(userPw);

		//발행
		registry.setPathMatcher(new AntPathMatcher("."));
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override //소켓연결 엔드포인트 경로와 cors설정
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws/chat")
			.setAllowedOriginPatterns("*");
		//.withSockJS();
	}
}
