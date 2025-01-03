package com.example.eatmate.app.domain.member.dto;


import com.example.eatmate.app.domain.member.domain.Gender;
import com.example.eatmate.app.domain.member.domain.Mbti;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class MemberSignUpRequestDto {
    //프로필 사진  추후 추가 예정
    @Min(1900) @Max(2100)
    private int year;

    @Min(1) @Max(12)
    private int month;

    @Min(1) @Max(31)
    private int day;

    @NotNull(message = "성별 선택은 필수 항목입니다.")
    private Gender gender;

    @NotNull(message = "전화번호 입력은 필수입니다.")
    @Pattern(regexp = "^010\\d{8}$", message = "전화번호는 010으로 시작하며 11자리 숫자여야 합니다.")
    private String phoneNumber;

    private Mbti mbti;

    @NotNull(message = "학번 입력은 필수입니다.")
    @Digits(integer = 9, fraction = 0, message = "9자리의 학번을 입력해주세요. 예 )202512345")
    private Long studentNumber;

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$", message = "닉네임은 한글, 영문, 숫자로 이루어진 2~12자여야 하며 공백이 없어야 합니다.")
    private String nickname;


}
