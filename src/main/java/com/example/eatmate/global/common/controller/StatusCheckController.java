package com.example.eatmate.global.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class StatusCheckController {
	@GetMapping("/health-check")
	public ResponseEntity<Void> checkHealthStatus() {

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
