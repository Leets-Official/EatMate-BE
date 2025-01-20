package com.example.eatmate.app.domain.block.dto;

import com.example.eatmate.app.domain.block.domain.Block;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BlockMemberResponseDto {
	private final Long blockId;
	private final Long blockedMemberId;
	private final String blockedUserNickname;

	@Builder
	private BlockMemberResponseDto(Long blockId, Long blockedMemberId, String blockedUserNickname) {
		this.blockId = blockId;
		this.blockedMemberId = blockedMemberId;
		this.blockedUserNickname = blockedUserNickname;
	}

	public static BlockMemberResponseDto createBlockMemberResponseDto(Block block) {
		return BlockMemberResponseDto.builder()
			.blockId(block.getId())
			.blockedMemberId(block.getBlockedMember().getMemberId())
			.blockedUserNickname(block.getBlockedMember().getNickname())
			.build();
	}
}
