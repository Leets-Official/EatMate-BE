package com.example.eatmate.app.domain.meeting.domain.repository;

import java.util.List;

import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.meeting.dto.CreatedMeetingListResponseDto;

public interface MeetingCustomRepository {
	List<CreatedMeetingListResponseDto> findAllMeetings(Long memberId, ParticipantRole role);
}
