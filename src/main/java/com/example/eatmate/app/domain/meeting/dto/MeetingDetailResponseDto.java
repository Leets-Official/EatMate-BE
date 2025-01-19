package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MeetingDetailResponseDto {
	private String meetingType;
	private String meetingName;
	private String meetingDescription;
	private GenderRestriction genderRestriction;
	private String location;
	private LocalDateTime dueDateTime;
	private String backgroundImage;
	private Boolean isOwner;
	private List<ParticipantDto> participants;

	@Builder
	private MeetingDetailResponseDto(String meetingType, String meetingName, String meetingDescription,
		GenderRestriction genderRestriction, String location, LocalDateTime dueDateTime,
		String backgroundImage, Boolean isOwner, List<ParticipantDto> participants) {
		this.meetingType = meetingType;
		this.meetingName = meetingName;
		this.meetingDescription = meetingDescription;
		this.genderRestriction = genderRestriction;
		this.location = location;
		this.dueDateTime = dueDateTime;
		this.backgroundImage = backgroundImage;
		this.isOwner = isOwner;
		this.participants = participants;
	}

	@Getter
	public static class ParticipantDto {
		private Long userId;
		private String name;
		private Boolean isOwner;
		private Boolean isCurrentUser;

		@Builder
		private ParticipantDto(Long userId, String name, Boolean isOwner, Boolean isCurrentUser) {
			this.userId = userId;
			this.name = name;
			this.isOwner = isOwner;
			this.isCurrentUser = isCurrentUser;
		}

		public static ParticipantDto createParticipantDto(Long userId, String name, Boolean isOwner,
			Boolean isCurrentUser) {
			return ParticipantDto.builder()
				.userId(userId)
				.name(name)
				.isOwner(isOwner)
				.isCurrentUser(isCurrentUser)
				.build();
		}
	}
}
