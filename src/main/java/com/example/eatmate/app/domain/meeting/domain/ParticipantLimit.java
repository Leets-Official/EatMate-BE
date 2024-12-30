package com.example.eatmate.app.domain.meeting.domain;

import org.hibernate.validator.constraints.Range;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ParticipantLimit {
	private boolean isUnlimited;

	@Column(nullable = true)
	@Range(min = 2, max = 10)
	private Long maxParticipants;
}
