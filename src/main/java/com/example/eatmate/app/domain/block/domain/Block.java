package com.example.eatmate.app.domain.block.domain;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "blocked_member_id")
	private Member blockedMember;

	@Builder
	private Block(Member member, Member blockedMember) {
		this.member = member;
		this.blockedMember = blockedMember;
	}

	public static Block createMemberBlock(Member member, Member blockedMember) {
		return Block.builder()
			.member(member)
			.blockedMember(blockedMember)
			.build();
	}
}
