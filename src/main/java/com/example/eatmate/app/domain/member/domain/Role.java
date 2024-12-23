package com.example.eatmate.app.domain.member.domain;

public enum Role {
    USER("사용자"),
    ADMIN("관리자");

    private final String RoleType;

    Role(String roleType) {
        this.RoleType = roleType;
    }

    public String getRoleType() {
        return RoleType;
    }

}
