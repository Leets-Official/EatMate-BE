package com.example.eatmate.app.domain.meeting.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;

public interface DeliveryMeetingRepository extends JpaRepository<DeliveryMeeting, Long> {
	List<DeliveryMeeting> findAllByFoodCategoryAndMeetingStatus(FoodCategory foodCategory,
		MeetingStatus meetingStatus);
}

