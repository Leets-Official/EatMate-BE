package com.example.eatmate.app.domain.block.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.block.domain.Block;
import com.example.eatmate.app.domain.block.domain.repository.BlockRepository;
import com.example.eatmate.app.domain.block.dto.BlockIdResponseDto;
import com.example.eatmate.app.domain.block.dto.BlockMeetingResponseDto;
import com.example.eatmate.app.domain.block.dto.BlockMemberResponseDto;
import com.example.eatmate.app.domain.block.dto.CreateMeetingBlockDto;
import com.example.eatmate.app.domain.block.dto.CreateMemberBlockDto;
import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingRepository;
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
	private final MeetingRepository meetingRepository;
	private final MemberRepository memberRepository;
	private final SecurityUtils securityUtils;

	@Transactional
	public BlockIdResponseDto blockMeeting(UserDetails userDetails, CreateMeetingBlockDto createMeetingBlockDto) {
		Member member = securityUtils.getMember(userDetails);

		Meeting meeting = meetingRepository.findById(createMeetingBlockDto.getMeetingId()).orElseThrow(
			() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));

		if (blockRepository.existsByMemberMemberIdAndMeetingId(member.getMemberId(), meeting.getId())) {
			throw new CommonException(ErrorCode.MEETING_ALREADY_BLOCKED);
		}

		Block block = Block.createMeetingBlock(member, meeting);
		// 이미 차단한 모임인지 확인

		blockRepository.save(block);
		return BlockIdResponseDto.from(block);
	}

	public BlockIdResponseDto blockMember(UserDetails userDetails, CreateMemberBlockDto createMemberBlockDto) {
		Member member = securityUtils.getMember(userDetails);

		Member blockedMember = memberRepository.findById(createMemberBlockDto.getMemberId()).orElseThrow(
			() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		if (blockRepository.existsByMemberMemberIdAndBlockedMemberMemberId(member.getMemberId(),
			blockedMember.getMemberId())) {
			throw new CommonException(ErrorCode.MEMBER_ALREADY_BLOCKED);
		}

		Block block = Block.createMemberBlock(member, blockedMember);
		// 이미 차단한 유저인지 확인

		blockRepository.save(block);

		return BlockIdResponseDto.from(block);
	}

	public List<BlockMeetingResponseDto> getMyBlockMeeting(UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);
		List<Block> myBlockedMeetings = blockRepository.findAllByMemberMemberIdAndMeetingIsNotNull(
			member.getMemberId());

		return myBlockedMeetings.stream()
			.map(BlockMeetingResponseDto::createBlockMeetingResponseDto)
			.toList();
	}

	public List<BlockMemberResponseDto> getMyBlockMember(UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);
		List<Block> myBlockedMembers = blockRepository.findAllByMemberMemberIdAndBlockedMemberMemberIdIsNotNull(
			member.getMemberId());

		return myBlockedMembers.stream()
			.map(BlockMemberResponseDto::createBlockMemberResponseDto)
			.toList();
	}
}
