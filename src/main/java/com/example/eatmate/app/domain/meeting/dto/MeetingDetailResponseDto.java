package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;

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
	private Boolean isCurrentUser;
	private List<ParticipantDto> participants;
	private OfflineMeetingCategory offlineMeetingCategory;
	private Long chatRoomId;

	@Builder
	private MeetingDetailResponseDto(String meetingType, String meetingName, String meetingDescription,
		GenderRestriction genderRestriction, String location, LocalDateTime dueDateTime,
		String backgroundImage, Boolean isOwner, Boolean isCurrentUser, List<ParticipantDto> participants,
		OfflineMeetingCategory offlineMeetingCategory, Long chatRoomId) {
		this.meetingType = meetingType;
		this.meetingName = meetingName;
		this.meetingDescription = meetingDescription;
		this.genderRestriction = genderRestriction;
		this.location = location;
		this.dueDateTime = dueDateTime;
		this.backgroundImage = backgroundImage;
		this.isOwner = isOwner;
		this.isCurrentUser = isCurrentUser;
		this.participants = participants;
		this.offlineMeetingCategory = offlineMeetingCategory;
		this.chatRoomId = chatRoomId;
	}

	@Getter
	public static class ParticipantDto {
		private Long userId;
		private String name;
		private String userProfileImage;
		private Boolean isOwner;
		private Boolean isCurrentUser;

		@Builder
		private ParticipantDto(Long userId, String name, Boolean isOwner, Boolean isCurrentUser,
			String userProfileImage) {
			this.userId = userId;
			this.name = name;
			this.userProfileImage = userProfileImage;
			this.isOwner = isOwner;
			this.isCurrentUser = isCurrentUser;
		}

		public static ParticipantDto createParticipantDto(Long userId, String name, String userProfileImage,
			Boolean isOwner,
			Boolean isCurrentUser) {
			return ParticipantDto.builder()
				.userId(userId)
				.name(name)
				.userProfileImage(userProfileImage)
				.isOwner(isOwner)
				.isCurrentUser(isCurrentUser)
				.build();
		}
	}
}
