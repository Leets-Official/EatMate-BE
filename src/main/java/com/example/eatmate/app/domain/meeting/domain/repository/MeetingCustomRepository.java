package com.example.eatmate.app.domain.meeting.domain.repository;

import java.util.List;

import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.meeting.dto.MeetingListResponseDto;

public interface MeetingCustomRepository {
	List<MeetingListResponseDto> findAllMeetings(Long memberId, ParticipantRole role, MeetingStatus meetingStatus);
}
