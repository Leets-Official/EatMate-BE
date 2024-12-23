package com.example.eatmate.app.domain.member.domain;

public enum Gender {
    MALE("남자"),
    FEMALE("여성");

    private final String genderType;


    Gender(String genderType) {
        this.genderType = genderType;
    }

    public String getGenderType() {
        return genderType;
    }
}
