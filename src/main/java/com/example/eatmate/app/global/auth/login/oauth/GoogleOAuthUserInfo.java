package com.example.eatmate.app.global.auth.login.oauth;

import java.util.Map;

public class GoogleOAuthUserInfo {

    private final Map<String, Object> attributes; // Google OAuth 사용자 정보 저장

    public GoogleOAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes; // 생성자를 통해 attributes 초기화
    }

    /**
     * Google에서 반환된 사용자 정보에서 이메일 추출
     */
    public String getEmail() {
        return (String) attributes.get("email"); // "email" 키로 이메일 반환
    }

    // attributes 반환
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 이름 반환
    public String getName() {
        return (String) attributes.get("name");
    }
}
