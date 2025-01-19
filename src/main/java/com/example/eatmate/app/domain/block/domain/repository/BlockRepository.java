package com.example.eatmate.app.domain.block.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.block.domain.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {
	Boolean existsByMemberMemberIdAndMeetingId(Long memberId, Long id);

	boolean existsByMemberMemberIdAndBlockedMemberMemberId(Long memberId, Long memberId1);

	List<Block> findAllByMemberMemberIdAndMeetingIsNotNull(Long memberId);
}
