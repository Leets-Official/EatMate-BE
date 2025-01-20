package com.example.eatmate.app.domain.block.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.block.domain.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {

	boolean existsByMemberMemberIdAndBlockedMemberMemberId(Long memberId, Long blockedMemberId);

	List<Block> findAllByMemberMemberIdAndBlockedMemberMemberIdIsNotNull(Long memberId);

	Block findByMemberMemberIdAndBlockedMemberMemberId(Long memberId, Long memberId1);
}
