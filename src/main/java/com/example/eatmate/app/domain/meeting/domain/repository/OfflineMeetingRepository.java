package com.example.eatmate.app.domain.meeting.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;

public interface OfflineMeetingRepository extends JpaRepository<OfflineMeeting, Long> {
	Slice<OfflineMeeting> findAllByOfflineMeetingCategoryAndMeetingStatus(OfflineMeetingCategory offlineMeetingCategory,
		MeetingStatus meetingStatus, Pageable pageable);

	List<OfflineMeeting> findByMeetingStatusAndMeetingDateBefore(MeetingStatus meetingStatus,
		LocalDateTime meetingDate);
}
