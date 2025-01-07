package com.example.eatmate.app.domain.meeting.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.meeting.domain.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingCustomRepository {
}
