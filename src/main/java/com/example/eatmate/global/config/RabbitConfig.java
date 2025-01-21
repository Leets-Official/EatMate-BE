package com.example.eatmate.global.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

	@Value("${rabbitmq.exchange}")
	private String CHAT_EXCHANGE_NAME;

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
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMissingQueuesFatal(false); // 큐가 없어도 애플리케이션 중단 방지
		factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		return factory;
	}

	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter() {
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
