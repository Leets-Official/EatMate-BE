package com.example.eatmate.app.domain.meeting.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;

public interface OfflineMeetingRepository extends JpaRepository<OfflineMeeting, Long> {
	List<OfflineMeeting> findByMeetingStatusAndMeetingDateBefore(MeetingStatus meetingStatus,
		LocalDateTime meetingDate);
}
