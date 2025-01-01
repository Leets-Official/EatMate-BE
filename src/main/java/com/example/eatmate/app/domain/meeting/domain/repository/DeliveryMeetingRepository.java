package com.example.eatmate.app.domain.meeting.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;

public interface DeliveryMeetingRepository extends JpaRepository<DeliveryMeeting, Long> {
}
