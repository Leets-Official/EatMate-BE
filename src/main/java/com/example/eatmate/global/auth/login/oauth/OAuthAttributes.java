package com.example.eatmate.global.auth.login.oauth;


import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.Role;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private final String nameAttributeKey;
    private final GoogleOAuthUserInfo googleOAuthUserInfo;


    private OAuthAttributes(String nameAttributeKey, GoogleOAuthUserInfo googleOAuthUserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.googleOAuthUserInfo = googleOAuthUserInfo;
    }

    public static OAuthAttributes of(String nameAttributeKey, Map<String, Object> attributes) {
        GoogleOAuthUserInfo googleOAuthUserInfo = new GoogleOAuthUserInfo(attributes);
        return new OAuthAttributes(nameAttributeKey, googleOAuthUserInfo);
    }

    /**
     * Google 사용자 정보를 기반으로 사용자 이메일 반환
     */
    public String getEmail() {
        return googleOAuthUserInfo.getEmail();
    }

    /**
     * Google 사용자 정보를 기반으로 사용자 이름 반환 (필요 시 추가)
     */
    public String getName() {
        return googleOAuthUserInfo.getAttributes().get("name").toString();
    }

    /**
     * Google 사용자 정보를 기반으로 엔티티 생성
     * @return Member 엔티티
     */

    public Member toEntity() {
        return Member.builder()
                .email(getEmail()) // 이메일
                .name(getName()) // 이름
                .role(Role.USER) // 기본 권한 설정
                .build();
    }

}
