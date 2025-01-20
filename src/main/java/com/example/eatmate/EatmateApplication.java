package com.example.eatmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EatmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(EatmateApplication.class, args);
	}

}
