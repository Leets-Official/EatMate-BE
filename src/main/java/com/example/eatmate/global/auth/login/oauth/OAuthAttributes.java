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

     // 기본값 처리를 위해 상수 생성
    private static final String DEFAULT_NAME = "Unnamed";
    private static final String DEFAULT_NICKNAME = "default_nickname";
    private static final String DEFAULT_PHONE_NUMBER = "010-0000-0000";
    private static final String DEFAULT_STUDENT_NUMBER = "default_student";
    private static final Role DEFAULT_ROLE = Role.GUEST;
    private static final boolean DEFAULT_IS_ACTIVE = false;

    @Builder
    private OAuthAttributes(String nameAttributeKey, GoogleOAuthUserInfo googleOAuthUserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.googleOAuthUserInfo = googleOAuthUserInfo;
    }

    public static OAuthAttributes of(String nameAttributeKey, Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .nameAttributeKey(nameAttributeKey) // nameAttributeKey 설정
                .googleOAuthUserInfo(new GoogleOAuthUserInfo(attributes)) // GoogleOAuthUserInfo 설정
                .build(); // 빌더로 OAuthAttributes 객체 생성
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
                .name(getName() != null ? getName() : DEFAULT_NAME) // 이름이 없으면 기본값 사용
                .nickname(DEFAULT_NICKNAME) // 기본 닉네임 설정
                .phoneNumber(DEFAULT_PHONE_NUMBER) // 기본 전화번호 설정
                .studentNumber(DEFAULT_STUDENT_NUMBER) // 기본 학번 설정
                .role(DEFAULT_ROLE) // 기본 Role 설정
                .isActive(DEFAULT_IS_ACTIVE) // 비활성화 상태로 설정
                .build();
    }

}
