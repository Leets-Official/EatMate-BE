package com.example.eatmate.app.domain.member.domain;

import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Mbti {
    ISTJ, ISFJ, INFJ, INTJ,
    ISTP, ISFP, INFP, INTP,
    ESTP, ESFP, ENFP, ENTP,
    ESTJ, ESFJ, ENFJ, ENTJ;

    @JsonCreator
    public static Mbti fromString(String mbti) {

        try {
            return Mbti.valueOf(mbti.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new CommonException(ErrorCode.INVALID_MBTI);
        }
    }
}
