package com.example.eatmate.app.domain.block.dto;

import com.example.eatmate.app.domain.block.domain.Block;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BlockIdResponseDto {
	private final Long blockId;

	@Builder
	public BlockIdResponseDto(Long blockId) {
		this.blockId = blockId;
	}

	public static BlockIdResponseDto from(Block block) {
		return BlockIdResponseDto.builder()
			.blockId(block.getId())
			.build();
	}

}
