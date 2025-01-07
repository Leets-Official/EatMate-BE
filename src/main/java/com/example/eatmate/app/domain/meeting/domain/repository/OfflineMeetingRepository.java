package com.example.eatmate.app.domain.meeting.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;

public interface OfflineMeetingRepository extends JpaRepository<OfflineMeeting, Long> {
	List<OfflineMeeting> findAllByOfflineMeetingCategoryAndMeetingStatus(OfflineMeetingCategory offlineMeetingCategory,
		MeetingStatus meetingStatus);
}
