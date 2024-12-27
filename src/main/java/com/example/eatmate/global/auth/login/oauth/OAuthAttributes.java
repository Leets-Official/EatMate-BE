package com.example.eatmate.global.auth.login.oauth;


import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private final String nameAttributeKey;
    private final GoogleOAuthUserInfo googleOAuthUserInfo;

    @Builder
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
                .email(getEmail()) // 구글에서 얻어온 이메일
                .name(getName() != null ? getName() : "Unnamed") // 이름이 없다면 기본값 설정
                .nickname("default_nickname") // 기본 닉네임 설정
                .phoneNumber("010-0000-0000") // 기본 전화번호 설정
                .studentNumber("default_student") // 기본 학번 설정
                .role(Role.GUEST) // 기본 Role 설정
                .isActive(false) // 비활성화 상태로 설정
                .build();
    }


}
