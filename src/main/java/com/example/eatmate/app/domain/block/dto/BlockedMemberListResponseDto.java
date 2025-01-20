package com.example.eatmate.app.domain.block.dto;

import com.example.eatmate.app.domain.block.domain.Block;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BlockedMemberListResponseDto {
	private final Long blockId;
	private final Long blockedMemberId;
	private final String blockedUserNickname;
	private final String profileImageUrl;

	@Builder
	private BlockedMemberListResponseDto(Long blockId, Long blockedMemberId, String blockedUserNickname,
		String profileImageUrl) {
		this.blockId = blockId;
		this.blockedMemberId = blockedMemberId;
		this.blockedUserNickname = blockedUserNickname;
		this.profileImageUrl = profileImageUrl;
	}

	public static BlockedMemberListResponseDto from(Block block) {
		return BlockedMemberListResponseDto.builder()
			.blockId(block.getId())
			.blockedMemberId(block.getBlockedMember().getMemberId())
			.blockedUserNickname(block.getBlockedMember().getNickname())
			.profileImageUrl(block.getBlockedMember().getProfileImage() != null ?
				block.getBlockedMember().getProfileImage().getImageUrl() : null)
			.build();
	}
}
