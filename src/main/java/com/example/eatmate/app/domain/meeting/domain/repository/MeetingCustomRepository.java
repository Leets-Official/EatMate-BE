package com.example.eatmate.app.domain.meeting.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.meeting.dto.MeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpcomingMeetingResponseDto;

public interface MeetingCustomRepository {
	List<MeetingListResponseDto> findAllMeetings(Long memberId, ParticipantRole role, MeetingStatus meetingStatus,
		Long lastMeetingId, LocalDateTime lastDateTime, int pageSize);

	UpcomingMeetingResponseDto findUpcomingMeeting(Long memberId);
}
