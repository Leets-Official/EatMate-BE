package com.example.eatmate.app.domain.meeting.scheduler;

import java.time.LocalDateTime;
import java.util.List;

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

	@Transactional
	@Scheduled(fixedRate = 300000) // 5분마다 실행
	public void updateMeetingStatus() {
		LocalDateTime now = LocalDateTime.now();
		try {
			updateDeliveryMeetingStatus(now);
		} catch (Exception e) {
			log.error("Meeting status update failed: now={}", now, e);
		}

		try {
			updateOfflineMeetingStatus(now);
		} catch (Exception e) {
			log.error("Meeting status update failed: now={}", now, e);
		}
	}

	private void updateDeliveryMeetingStatus(LocalDateTime now) {
		List<DeliveryMeeting> expiredDeliveryMeetings = deliveryMeetingRepository
			.findByMeetingStatusAndOrderDeadlineBefore(MeetingStatus.ACTIVE, now);

		for (DeliveryMeeting meeting : expiredDeliveryMeetings) {
			meeting.deleteMeeting();
			log.info("DeliveryMeeting status updated to INACTIVE: meetingId={}", meeting.getId());
		}
	}

	private void updateOfflineMeetingStatus(LocalDateTime now) {
		List<OfflineMeeting> expiredOfflineMeetings = offlineMeetingRepository
			.findByMeetingStatusAndMeetingDateBefore(MeetingStatus.ACTIVE, now);

		for (OfflineMeeting meeting : expiredOfflineMeetings) {
			meeting.deleteMeeting();
			log.info("OfflineMeeting status updated to INACTIVE: meetingId={}", meeting.getId());
		}
	}
}
