package com.example.eatmate.app.domain.member.domain;

import com.example.eatmate.global.config.error.exception.CommonException;
import com.example.eatmate.global.config.error.exception.custom.InvalidMbtiException;

public enum Mbti {
    ISTJ, ISFJ, INFJ, INTJ,
    ISTP, ISFP, INFP, INTP,
    ESTP, ESFP, ENFP, ENTP,
    ESTJ, ESFJ, ENFJ, ENTJ;


    public static Mbti fromString(String mbti) {

        try {
            return Mbti.valueOf(mbti.toUpperCase());
        } catch (CommonException e) {
            throw new InvalidMbtiException();
        }
    }
}
