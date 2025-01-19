package com.example.eatmate.app.domain.block.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateMemberBlockDto {
	@NotBlank
	private Long blockId;
}
