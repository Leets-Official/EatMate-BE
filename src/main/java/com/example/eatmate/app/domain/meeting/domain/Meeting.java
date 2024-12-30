package com.example.eatmate.app.domain.meeting.domain;

import org.hibernate.validator.constraints.Range;

import com.example.eatmate.app.domain.member.domain.Gender;
import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public abstract class Meeting extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 30, nullable = false)
	private String meetingName;

	@Column(length = 100)
	private String description;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private GenderRestriction genderRestriction;

	@Column
	@Range(min = 2, max = 10)
	@Embedded
	private ParticipantLimit participantLimit;

}

