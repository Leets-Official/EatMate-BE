package com.example.eatmate.app.domain.meeting.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.meeting.domain.MeetingParticipant;
import com.example.eatmate.app.domain.member.domain.Member;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

	Optional<MeetingParticipant> findByMeetingIdAndMember(Long meetingId, Member member);

	Long countByMeetingId(Long meetingId);
}
