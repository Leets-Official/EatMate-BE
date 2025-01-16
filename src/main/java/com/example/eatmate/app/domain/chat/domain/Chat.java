package com.example.eatmate.app.domain.chat.domain;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.global.common.BaseTimeEntity;
import com.example.eatmate.global.common.DeletedStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
public class Chat extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	@NotNull
	private String content;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private Member sender;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatRoom_id")
	private ChatRoom chatRoom;

	@NotNull
	@Column
	@Enumerated(EnumType.STRING)
	private DeletedStatus deletedStatus;

	@Builder
	private Chat(String content, Member sender, ChatRoom chatRoom) {
		this.content = content;
		this.sender = sender;
		this.chatRoom = chatRoom;
		this.deletedStatus = DeletedStatus.NOT_DELETED;
	}

	public static Chat createChat(String content, Member sender, ChatRoom chatRoom) {
		return Chat.builder()
			.content(content)
			.sender(sender)
			.chatRoom(chatRoom)
			.build();
	}

	public void deleteChat() {
		this.deletedStatus = DeletedStatus.DELETED;
	}
}
