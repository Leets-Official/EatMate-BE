package com.example.eatmate.app.domain.meeting.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.app.domain.meeting.domain.MeetingParticipant;
import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.member.domain.Member;

import jakarta.persistence.LockModeType;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

	Long countByMeeting_Id(Long meetingId);

	Optional<MeetingParticipant> findByMeetingAndRole(Meeting meeting, ParticipantRole role);

	Optional<MeetingParticipant> findByMeetingAndMember(Meeting meeting, Member member);

	boolean existsByMeetingAndMember(Meeting meeting, Member member);

	Long countByMeetingAndRoleNot(Meeting meeting, ParticipantRole role);

	List<MeetingParticipant> findByMeeting(Meeting meeting);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT mp FROM MeetingParticipant mp WHERE mp.meeting.id = :meetingId")
	List<MeetingParticipant> findParticipantsByMeetingIdWithLock(Long meetingId);

}
