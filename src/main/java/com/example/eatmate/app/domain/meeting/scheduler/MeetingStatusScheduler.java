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
	@Scheduled(fixedRate = 60000)
	public void updateOfflineMeetingStatus(LocalDateTime now) {
		try {
			List<OfflineMeeting> expiredOfflineMeetings = offlineMeetingRepository
				.findByMeetingStatusAndMeetingDateBefore(MeetingStatus.ACTIVE, now);

			for (OfflineMeeting meeting : expiredOfflineMeetings) {
				try {
					meeting.deleteMeeting();
					log.info("OfflineMeeting status updated to INACTIVE: meetingId={}", meeting.getId());
				} catch (Exception e) {
					log.error("OfflineMeeting status update failed: meetingId={}", meeting.getId());
				}
			}
		} catch (Exception e) {
			log.error("Failed to fetch expired offline meetings: {}", e.getMessage());
		}
	}

	@Transactional
	@Scheduled(fixedRate = 60000)
	public void updateDeliveryMeetingStatus(LocalDateTime now) {
		try {
			List<DeliveryMeeting> expiredDeliveryMeetings = deliveryMeetingRepository
				.findByMeetingStatusAndOrderDeadlineBefore(MeetingStatus.ACTIVE, now);

			for (DeliveryMeeting meeting : expiredDeliveryMeetings) {
				try {
					meeting.deleteMeeting();
					log.info("DeliveryMeeting status updated to INACTIVE: meetingId={}", meeting.getId());
				} catch (Exception e) {
					log.error("DeliveryMeeting status update failed: meetingId={}", meeting.getId());
				}
			}
		} catch (Exception e) {
			log.error("Failed to fetch expired delivery meetings: {}", e.getMessage());
		}
	}
}
