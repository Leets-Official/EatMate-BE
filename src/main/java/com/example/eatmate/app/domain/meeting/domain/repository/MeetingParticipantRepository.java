package com.example.eatmate.app.domain.meeting.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.meeting.domain.MeetingParticipant;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

	Optional<MeetingParticipant> findByMeetingIdAndUserId(Long meetingId, Long userId);
	Long countByMeetingId(Long meetingId);
}
