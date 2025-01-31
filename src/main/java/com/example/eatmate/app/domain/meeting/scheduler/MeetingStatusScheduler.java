package com.example.eatmate.app.domain.meeting.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.meeting.domain.repository.DeliveryMeetingRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.OfflineMeetingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingStatusScheduler {

	private final DeliveryMeetingRepository deliveryMeetingRepository;
	private final OfflineMeetingRepository offlineMeetingRepository;

	@Async
	@Scheduled(fixedRate = 60000)
	@Transactional
	public void updateOfflineMeetingStatus() {
		try {
			List<OfflineMeeting> expiredOfflineMeetings = offlineMeetingRepository
				.findByMeetingStatusAndMeetingDateBefore(MeetingStatus.ACTIVE, LocalDateTime.now());

			for (OfflineMeeting meeting : expiredOfflineMeetings) {
				meeting.deleteMeeting();
				log.info("OfflineMeeting status updated to INACTIVE: meetingId={}, thread={}", meeting.getId(),
					Thread.currentThread().getName());
			}
		} catch (Exception e) {
			log.error("Failed to update offline meetings status: {}", e.getMessage());
		}
	}

	@Async
	@Scheduled(fixedRate = 60000)
	@Transactional
	public void updateDeliveryMeetingStatus() {
		try {
			List<DeliveryMeeting> expiredDeliveryMeetings = deliveryMeetingRepository
				.findByMeetingStatusAndOrderDeadlineBefore(MeetingStatus.ACTIVE, LocalDateTime.now());

			for (DeliveryMeeting meeting : expiredDeliveryMeetings) {
				meeting.deleteMeeting();
				log.info("Delivery status updated to INACTIVE: meetingId={}, thread={}", meeting.getId(),
					Thread.currentThread().getName());
			}
		} catch (Exception e) {
			log.error("Failed to update delivery meetings status: {}", e.getMessage());
		}
	}
}
