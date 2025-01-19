package com.example.eatmate.app.domain.block.dto;

import com.example.eatmate.app.domain.block.domain.Block;
import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BlockMeetingResponseDto {
	private final Long blockId;
	private String meetingType;
	private Long meetingId;
	private String meetingName;
	private MeetingStatus meetingStatus;

	@Builder
	private BlockMeetingResponseDto(Long id, String meetingType, Long meetingId, String meetingName,
		MeetingStatus meetingStatus) {
		this.blockId = id;
		this.meetingType = meetingType;
		this.meetingId = meetingId;
		this.meetingName = meetingName;
		this.meetingStatus = meetingStatus;

	}

	public static BlockMeetingResponseDto createBlockMeetingResponseDto(Block block) {
		return BlockMeetingResponseDto.builder()
			.id(block.getId())
			.meetingType(block.getMeeting().getType())
			.meetingId(block.getMeeting().getId())
			.meetingName(block.getMeeting().getMeetingName())
			.meetingStatus(block.getMeeting().getMeetingStatus())
			.build();
	}
}
