package com.example.eatmate.global.auth.login.oauth;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleOAuthUserInfo {

	private Map<String, Object> attributes = new HashMap<>();

	// 기본 생성자가 있어야 Jackson이 객체 생성 후, setter로 값을 주입할 수 있음.
	public GoogleOAuthUserInfo() {
	}

	// 기존 생성자 유지 (선택 사항)
	public GoogleOAuthUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@JsonAnySetter
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public String getEmail() {
		Object email = attributes.get("email");
		return email != null ? email.toString() : null;
	}

	public String getName() {
		Object name = attributes.get("name");
		return name != null ? name.toString() : null;
	}
}
