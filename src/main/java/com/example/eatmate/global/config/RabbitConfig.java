package com.example.eatmate.global.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

	@Value("${rabbitmq.exchange}")
	private String CHAT_EXCHANGE_NAME;
	@Value("${rabbitmq.queue-prefix}")
	private String queuePrefix;
	@Value("${rabbitmq.binding-key-prefix}")
	private String bindingKeyPrefix;
	@Value("${spring.rabbitmq.username}")
	private String userName;
	@Value("${spring.rabbitmq.password}")
	private String userPw;
	@Value("${spring.rabbitmq.host}")
	private String host;
	@Value("${spring.rabbitmq.port}")
	private String port;

	@Bean
	public AmqpAdmin amqpAdmin() {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
		rabbitAdmin.declareExchange(chatExchange());
		return rabbitAdmin;
	}

	@Bean
	public TopicExchange chatExchange() {
		return new TopicExchange(CHAT_EXCHANGE_NAME, true, false);
	}
	//큐 생성(채팅방 생성과 동시에)
	public void createQueueForChatRoom(Long chatRoomId) {

		String queueName = queuePrefix + chatRoomId;
		String bindingKey = bindingKeyPrefix + chatRoomId;

		// DLQ 설정 추가
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", "dlx.exchange"); // Dead Letter Exchange 설정
		args.put("x-dead-letter-routing-key", "dead.letter.queue"); // Dead Letter Queue 라우팅 키
		//args.put("x-message-ttl", 60000);//지속 시간 1분

		Queue chatQueue = new Queue(queueName, true, false, false, args);
		amqpAdmin().declareQueue(chatQueue);

		Binding binding = BindingBuilder.bind(chatQueue)
			.to(chatExchange())
			.with(bindingKey);
		amqpAdmin().declareBinding(binding);
	}
	//채팅방 나가기(모임 종료 시) 큐 할당 해제
	public void deleteQueueForChatRoom(Long chatRoomId) {
		String queueName = queuePrefix + chatRoomId;
		amqpAdmin().deleteQueue(queueName);
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setMessageConverter(jsonMessageConverter());
		template.setExchange(CHAT_EXCHANGE_NAME);
		return template;
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost(host);
		factory.setUsername(userName);
		factory.setPassword(userPw);
		factory.setPort(Integer.parseInt(port));
		return factory;
	}

	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter(){
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public Queue deadLetterQueue() {
		return new Queue("dead.letter.queue", true);
	}

	@Bean
	public DirectExchange deadLetterExchange() {
		return new DirectExchange("dlx.exchange");
	}

	@Bean
	public Binding dlqBinding() {
		return BindingBuilder.bind(deadLetterQueue())
			.to(deadLetterExchange())
			.with("dead.letter.queue");
	}
}
