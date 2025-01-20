package com.example.eatmate.app.domain.block.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UnblockMemberRequestDto {
	@NotNull
	private Long memberId;
}
