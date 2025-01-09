package com.example.eatmate.global.config.error.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("예외 발생시 GlobalExceptionHandler가 처리하여 깃허브 이슈를 생성한다")
	void handleGenericException() throws Exception {
		// given
		String url = "/api/test-exception";

		// when & then
		mockMvc.perform(get(url))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(500))
			.andExpect(jsonPath("$.message").exists())
			.andDo(print());
	}
}

@RestController
class TestExceptionController {

	@GetMapping("/api/test-exception")
	public void throwException() {
		throw new RuntimeException("테스트용 예외 발생!");
	}
}
