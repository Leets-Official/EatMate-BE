package com.example.eatmate.app.domain.meeting.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.meeting.domain.repository.DeliveryMeetingRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.OfflineMeetingRepository;

@ExtendWith(MockitoExtension.class)
class MeetingStatusSchedulerTest {

	@Mock
	private DeliveryMeetingRepository deliveryMeetingRepository;

	@Mock
	private OfflineMeetingRepository offlineMeetingRepository;

	@InjectMocks
	private MeetingStatusScheduler scheduler;

	private LocalDateTime now;
	private DeliveryMeeting expiredDeliveryMeeting;
	private DeliveryMeeting activeDeliveryMeeting;
	private OfflineMeeting expiredOfflineMeeting;
	private OfflineMeeting activeOfflineMeeting;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();

		expiredDeliveryMeeting = DeliveryMeeting.builder()
			.meetingStatus(MeetingStatus.ACTIVE)
			.id(1L)
			.orderDeadline(now.minusHours(1))
			.build();

		activeDeliveryMeeting = DeliveryMeeting.builder()
			.meetingStatus(MeetingStatus.ACTIVE)
			.id(2L)
			.orderDeadline(now.plusHours(1))
			.build();

		expiredOfflineMeeting = OfflineMeeting.builder()
			.meetingStatus(MeetingStatus.ACTIVE)
			.id(1L)
			.meetingDate(now.minusHours(1))
			.build();

		activeOfflineMeeting = OfflineMeeting.builder()
			.meetingStatus(MeetingStatus.ACTIVE)
			.id(2L)
			.meetingDate(now.plusHours(1))
			.build();
	}

	@Test
	@DisplayName("배달 모임이 만료되면 상태가 INACTIVE로 변경되어야 한다")
	void shouldUpdateExpiredDeliveryMeetingStatus() {
		// given
		when(deliveryMeetingRepository.findByMeetingStatusAndOrderDeadlineBefore(
			eq(MeetingStatus.ACTIVE), any(LocalDateTime.class)))
			.thenReturn(List.of(expiredDeliveryMeeting));

		// when
		scheduler.updateMeetingStatus();

		// then
		assertEquals(MeetingStatus.INACTIVE, expiredDeliveryMeeting.getMeetingStatus());
		verify(deliveryMeetingRepository, times(1))
			.findByMeetingStatusAndOrderDeadlineBefore(eq(MeetingStatus.ACTIVE), any(LocalDateTime.class));
	}

	@Test
	@DisplayName("오프라인 모임이 만료되면 상태가 INACTIVE로 변경되어야 한다")
	void shouldUpdateExpiredOfflineMeetingStatus() {
		// given
		when(offlineMeetingRepository.findByMeetingStatusAndMeetingDateBefore(
			eq(MeetingStatus.ACTIVE), any(LocalDateTime.class)))
			.thenReturn(List.of(expiredOfflineMeeting));

		// when
		scheduler.updateMeetingStatus();

		// then
		assertEquals(MeetingStatus.INACTIVE, expiredOfflineMeeting.getMeetingStatus());
		verify(offlineMeetingRepository, times(1))
			.findByMeetingStatusAndMeetingDateBefore(eq(MeetingStatus.ACTIVE), any(LocalDateTime.class));
	}

	@Test
	@DisplayName("활성 상태의 모임은 상태가 변경되지 않아야 한다")
	void shouldNotUpdateActiveMeetingStatus() {
		// given
		when(deliveryMeetingRepository.findByMeetingStatusAndOrderDeadlineBefore(
			eq(MeetingStatus.ACTIVE), any(LocalDateTime.class)))
			.thenReturn(Collections.emptyList());
		when(offlineMeetingRepository.findByMeetingStatusAndMeetingDateBefore(
			eq(MeetingStatus.ACTIVE), any(LocalDateTime.class)))
			.thenReturn(Collections.emptyList());

		// when
		scheduler.updateMeetingStatus();

		// then
		assertEquals(MeetingStatus.ACTIVE, activeDeliveryMeeting.getMeetingStatus());
		assertEquals(MeetingStatus.ACTIVE, activeOfflineMeeting.getMeetingStatus());
		verify(deliveryMeetingRepository, times(1))
			.findByMeetingStatusAndOrderDeadlineBefore(eq(MeetingStatus.ACTIVE), any(LocalDateTime.class));
		verify(offlineMeetingRepository, times(1))
			.findByMeetingStatusAndMeetingDateBefore(eq(MeetingStatus.ACTIVE), any(LocalDateTime.class));
	}

	@Test
	@DisplayName("예외가 발생해도 다른 처리에 영향을 주지 않아야 한다")
	void shouldHandleExceptionsGracefully() {
		// given
		when(deliveryMeetingRepository.findByMeetingStatusAndOrderDeadlineBefore(any(), any()))
			.thenThrow(new RuntimeException("Database error"));
		when(offlineMeetingRepository.findByMeetingStatusAndMeetingDateBefore(any(), any()))
			.thenReturn(List.of(expiredOfflineMeeting));

		// when
		scheduler.updateMeetingStatus();

		// then
		assertEquals(MeetingStatus.INACTIVE, expiredOfflineMeeting.getMeetingStatus());
		verify(deliveryMeetingRepository, times(1))
			.findByMeetingStatusAndOrderDeadlineBefore(any(), any());
		verify(offlineMeetingRepository, times(1))
			.findByMeetingStatusAndMeetingDateBefore(any(), any());
	}
}
