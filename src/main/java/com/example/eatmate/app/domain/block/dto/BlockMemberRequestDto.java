package com.example.eatmate.app.domain.block.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BlockMemberRequestDto {
	@NotNull
	private Long memberId;
}
