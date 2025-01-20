package com.example.eatmate.app.domain.block.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.block.domain.Block;
import com.example.eatmate.app.domain.block.domain.repository.BlockRepository;
import com.example.eatmate.app.domain.block.dto.BlockIdResponseDto;
import com.example.eatmate.app.domain.block.dto.BlockMemberRequestDto;
import com.example.eatmate.app.domain.block.dto.BlockMemberResponseDto;
import com.example.eatmate.app.domain.block.dto.UnblockMemberRequestDto;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.common.util.SecurityUtils;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockService {

	private final BlockRepository blockRepository;

	private final MemberRepository memberRepository;

	private final SecurityUtils securityUtils;

	@Transactional
	public BlockIdResponseDto blockMember(UserDetails userDetails, BlockMemberRequestDto blockMemberRequestDto) {
		Member member = securityUtils.getMember(userDetails);

		Member blockedMember = memberRepository.findById(blockMemberRequestDto.getMemberId()).orElseThrow(
			() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		// 이미 차단한 유저인지 확인
		if (blockRepository.existsByMemberMemberIdAndBlockedMemberMemberId(member.getMemberId(),
			blockedMember.getMemberId())) {
			throw new CommonException(ErrorCode.MEMBER_ALREADY_BLOCKED);
		}

		Block block = Block.createMemberBlock(member, blockedMember);

		blockRepository.save(block);

		return BlockIdResponseDto.from(block);
	}

	public List<BlockMemberResponseDto> getMyBlockMember(UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);
		List<Block> myBlockedMembers = blockRepository.findAllByMemberMemberIdAndBlockedMemberMemberIdIsNotNull(
			member.getMemberId());

		return myBlockedMembers.stream()
			.map(BlockMemberResponseDto::createBlockMemberResponseDto)
			.toList();
	}

	public void unblockMember(UserDetails userDetails, UnblockMemberRequestDto unblockMemberRequestDto) {
		Member member = securityUtils.getMember(userDetails);

		Member blockedMember = memberRepository.findById(unblockMemberRequestDto.getMemberId()).orElseThrow(
			() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		Block block = blockRepository.findByMemberMemberIdAndBlockedMemberMemberId(member.getMemberId(),
			blockedMember.getMemberId());

		if (block == null) {    // 차단하지 않은 멤버일 경우
			throw new CommonException(ErrorCode.UNBLOCK_UNBLOCKED_MEMBER);
		}

		blockRepository.delete(block);
	}
}
