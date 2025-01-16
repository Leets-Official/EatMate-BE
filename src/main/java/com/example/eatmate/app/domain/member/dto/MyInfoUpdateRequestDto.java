package com.example.eatmate.app.domain.member.dto;

import com.example.eatmate.app.domain.member.domain.Mbti;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MyInfoUpdateRequestDto {

	@Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$", message = "닉네임은 한글, 영문, 숫자로 이루어진 2~12자여야 하며 공백이 없어야 합니다.")
	private String nickname;

	private Mbti mbti;

}
