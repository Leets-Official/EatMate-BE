package com.example.eatmate.app.domain.chatRoom.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column
	private Long ownerId;

	@Column
	private String lastChat;

	@Column
	private LocalDateTime lastChatAt;

	@NotNull
	@OneToOne(mappedBy = "chatRoom")
	private Meeting meeting;

	@NotNull
	@Column
	@Enumerated(EnumType.STRING)
	private DeletedStatus deletedStatus;

	@OneToMany(mappedBy = "chatRoom")
	private List<MemberChatRoom> participant;

	@Builder
	private ChatRoom(Long ownerId, Meeting meeting) {
		this.ownerId = ownerId;
		this.meeting = meeting;
		this.deletedStatus = DeletedStatus.NOT_DELETED;
	}

	public static ChatRoom createChatRoom(Long host, Meeting meeting) {
		return ChatRoom.builder()
			.ownerId(host)
			.meeting(meeting)
			.build();
	}

	public void deleteChatRoom() {
		this.deletedStatus = DeletedStatus.DELETED;
	}

	public void addParticipant(MemberChatRoom participant) {
		if (this.participant == null) {
			this.participant = new ArrayList<>();
		}
		this.participant.add(participant);
	}

	public void removeParticipant(MemberChatRoom participant) {
		if (this.participant != null) {
			this.participant.remove(participant);
		}
	}

	public void updateLastChat(String lastChat) {
		this.lastChat = lastChat;
	}

	public void updateLastChatAt(LocalDateTime lastChatAt) {
		this.lastChatAt = lastChatAt;
	}
}
