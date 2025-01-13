package com.example.eatmate.global.config.error.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.dto.IssueCreateRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GithubIssueGenerator {
	private static final String REPO_URL = "https://api.github.com/repos/Leets-Official/EatMate-BE/issues";
	private static final List<String> ASSIGNEES = List.of("ehs208", "seokjun01", "jj0526", "dyk-im");
	private static final List<String> LABELS = List.of("fix");
	private static final MediaType JSON_MEDIA_TYPE = new MediaType("application", "json", StandardCharsets.UTF_8);
	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;
	@Value("${github.access-token}")
	private String accessToken;

	public void create(Exception exception) {
		// api 요청
		restTemplate.exchange(
			REPO_URL,
			HttpMethod.POST,
			new HttpEntity<>(createRequestJson(exception), setAuthorization()),
			String.class
		);
	}

	// DTO 객체를 JSON 문자열로 변환
	private String createRequestJson(Exception exception) {
		return parseJsonString(new IssueCreateRequestDto(
			"[Fix] 서버 장애 발생 " + exception.getMessage(),
			createIssueBody(exception),
			ASSIGNEES,
			LABELS
		));
	}

	private String parseJsonString(IssueCreateRequestDto request) {
		try {
			return objectMapper.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			throw new CommonException(ErrorCode.JSON_PARSING_ERROR);
		}
	}

	// 발생한 예외의 stackTrace를 마크다운 코드 블럭으로 감싸서 작성
	private String createIssueBody(Exception exception) {
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		return "```\n" + stringWriter + "\n```";
	}

	// Authorization 헤더에 accessToken 입력
	private HttpHeaders setAuthorization() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization", "token " + accessToken);
		httpHeaders.setContentType(JSON_MEDIA_TYPE);
		return httpHeaders;
	}
}
