package com.example.eatmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class EatmateApplication  {

	public static void main(String[] args) {
		SpringApplication.run(EatmateApplication.class, args);
	}

}
