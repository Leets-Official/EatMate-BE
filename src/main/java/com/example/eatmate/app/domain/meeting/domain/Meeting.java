package com.example.eatmate.app.domain.meeting.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.image.domain.Image;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "meeting_type")
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public abstract class Meeting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "meeting_type", insertable = false, updatable = false)
	private String type;  // discriminator 값을 조회하기 위한 필드

	@Column(length = 30, nullable = false)
	private String meetingName;

	@Column(length = 100)
	private String meetingDescription;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private GenderRestriction genderRestriction;

	@Column
	@Embedded
	private ParticipantLimit participantLimit;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MeetingStatus meetingStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MeetingBackgroundType backgroundType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "background_image_id")
	private Image backgroundImage;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatRoom;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column
	private LocalDateTime updatedAt;

	// 모임 수정
	public void updateMeeting(String meetingName, String meetingDescription, Image backgroundImage) {
		this.meetingName = meetingName;
		this.meetingDescription = meetingDescription;
		this.backgroundImage = backgroundImage;
	}

	// 모임 삭제
	public void deleteMeeting() {
		this.meetingStatus = MeetingStatus.INACTIVE;
	}

	// 채팅방 등록
	public void setChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
	}
}

