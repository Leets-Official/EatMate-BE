package com.example.eatmate.app.domain.meeting.domain;

import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class MeetingParticipant extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meeting_id")
	private Meeting meeting;

	@Enumerated(EnumType.STRING)
	private ParticipantRole role;  // HOST, PARTICIPANT 등

	@Builder
	private MeetingParticipant(Member member, Meeting meeting, ParticipantRole role) {
		this.member = member;
		this.meeting = meeting;
		this.role = role;
	}

	public static MeetingParticipant createMeetingParticipant(Member member, Meeting meeting, ParticipantRole role) {
		return MeetingParticipant.builder()
			.member(member)
			.meeting(meeting)
			.role(role)
			.build();
	}

}

