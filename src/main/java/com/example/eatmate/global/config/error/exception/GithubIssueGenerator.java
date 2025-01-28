package com.example.eatmate.global.config.error.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	// 기존 이슈 조회를 위한 메서드 추가
	private boolean isExistingIssue(String title) {
		try {
			ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
				REPO_URL,
				HttpMethod.GET,
				new HttpEntity<>(setAuthorization()),
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				}
			);

			return response.getBody().stream()
				.map(issue -> (String)issue.get("title"))
				.anyMatch(issueTitle -> issueTitle.equals(title));
		} catch (Exception e) {
			return false;
		}
	}

	public void create(Exception exception) {
		String title = "[Fix] 서버 장애 발생 " + exception.getMessage();

		// 동일한 제목의 이슈가 있는지 확인
		if (isExistingIssue(title)) {
			return;
		}

		restTemplate.exchange(
			REPO_URL,
			HttpMethod.POST,
			new HttpEntity<>(createRequestJson(exception, getErrorLocation(exception)), setAuthorization()),
			String.class
		);
	}

	// DTO 객체를 JSON 문자열로 변환
	private String createRequestJson(Exception exception, String errorLocation) {
		return parseJsonString(new IssueCreateRequestDto(
			"[Fix] 서버 장애 발생 " + exception.getMessage(),
			createIssueBody(exception, errorLocation),
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
	private String createIssueBody(Exception exception, String errorLocation) {
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		return String.format("## Error Location\n%s\n\n## Stack Trace\n```\n%s\n```",
			errorLocation,
			stringWriter);
	}

	// 에러 발생 위치를 찾는 메서드 추가
	private String getErrorLocation(Exception exception) {
		StackTraceElement[] stackTrace = exception.getStackTrace();
		for (StackTraceElement element : stackTrace) {
			// 프로젝트 패키지에 해당하는 첫 번째 스택트레이스를 찾음
			if (element.getClassName().startsWith("com.example")) {  // 프로젝트 패키지명에 맞게 수정
				return String.format("%s.%s(line: %d)",
					element.getClassName(),
					element.getMethodName(),
					element.getLineNumber());
			}
		}
		return "Unknown location";
	}

	// Authorization 헤더에 accessToken 입력
	private HttpHeaders setAuthorization() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization", "token " + accessToken);
		httpHeaders.setContentType(JSON_MEDIA_TYPE);
		return httpHeaders;
	}
}
