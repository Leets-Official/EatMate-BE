package com.example.eatmate.app.domain.meeting.domain;

import org.hibernate.validator.constraints.Range;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class ParticipantLimit {

	private boolean isLimited;

	@Column(nullable = true)
	@Range(min = 2, max = 10)
	private Long maxParticipants;

	@Builder
	private ParticipantLimit(boolean isLimited, Long maxParticipants) {
		this.isLimited = isLimited;
		this.maxParticipants = maxParticipants;
	}
}
