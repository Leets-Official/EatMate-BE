package com.example.eatmate.global.auth.login.oauth;


import com.example.eatmate.app.domain.member.domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

//OAuth2 인증 후 사용자 정보를 확장하여 반환하려는 목적
//Spring Security의 DefaultOAuth2User를 상속받아, 사용자 정보를 커스터마이징.

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String email;
    private Role role;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            Role role) {
        super(authorities, attributes, nameAttributeKey);
        this.email = attributes.get("email").toString();
        this.role = role; // DB에서 전달된 Role 값을 사용
    }
}
