package com.example.eatmate.app.domain.meeting.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.eatmate.app.domain.image.domain.Image;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@OneToOne
	@JoinColumn(name = "background_image_id")
	private Image backgroundImage;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column
	private LocalDateTime updatedAt;

}

