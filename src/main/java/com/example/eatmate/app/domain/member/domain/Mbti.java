package com.example.eatmate.app.domain.member.domain;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;


@Getter
public enum Mbti {
    ISTJ("ISTJ"), ISFJ("ISFJ"), INFJ("INFJ"), INTJ("INTJ"),
    ISTP("ISTP"), ISFP("ISFP"), INFP("INFP"), INTP("INTP"),
    ESTP("ESTP"), ESFP("ESFP"), ENFP("ENFP"), ENTP("ENTP"),
    ESTJ("ESTJ"), ESFJ("ESFJ"), ENFJ("ENFJ"), ENTJ("ENTJ");

    private final String mbtiType;

    Mbti(String mbtiType) {
        this.mbtiType = mbtiType;
    }

}
