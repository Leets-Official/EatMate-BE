package com.example.eatmate.app.domain.chatRoom.domain;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberChatRoom extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "chatRoom_id")
	private ChatRoom chatRoom;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	private MemberChatRoom(ChatRoom chatRoom, Member member) {
		this.chatRoom = chatRoom;
		this.member = member;
	}

	public static MemberChatRoom create(ChatRoom chatRoom, Member member) {
		return MemberChatRoom.builder()
			.member(member)
			.chatRoom(chatRoom)
			.build();
	}
}
