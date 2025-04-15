package com.example.eatmate.app.domain.meeting.domain.repository;

import static com.example.eatmate.app.domain.meeting.domain.GenderRestriction.*;
import static com.example.eatmate.app.domain.meeting.domain.MeetingBackgroundType.*;
import static com.example.eatmate.app.domain.meeting.domain.MeetingStatus.*;
import static com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory.*;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.querydsl.jpa.impl.JPAQueryFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OfflineMeetingRepositoryTest {

	@Autowired
	private OfflineMeetingRepository offlineMeetingRepository;

	@MockitoBean
	private JPAQueryFactory jpaQueryFactory;

	@BeforeEach
	void setUp() {
		OfflineMeeting offlineMeeting1 = OfflineMeeting.builder()
			.offlineMeetingCategory(BEVERAGE)
			.meetingDate(LocalDateTime.now().plusDays(1))
			.meetingStatus(INACTIVE)
			.meetingPlace("서울시 강남구")
			.meetingName("오프라인 모임1")
			.genderRestriction(ALL)
			.backgroundType(DEFAULT_IMAGE_1)
			.build();

		OfflineMeeting offlineMeeting2 = OfflineMeeting.builder()
			.offlineMeetingCategory(BEVERAGE)
			.meetingDate(LocalDateTime.now().plusDays(1))
			.meetingStatus(ACTIVE)
			.meetingPlace("서울시 강남구")
			.meetingName("오프라인 모임2")
			.genderRestriction(ALL)
			.backgroundType(DEFAULT_IMAGE_1)
			.build();

		OfflineMeeting offlineMeeting3 = OfflineMeeting.builder()
			.offlineMeetingCategory(BEVERAGE)
			.meetingDate(LocalDateTime.now().minusDays(1))
			.meetingStatus(INACTIVE)
			.meetingPlace("서울시 강남구")
			.meetingName("오프라인 모임3")
			.genderRestriction(ALL)
			.backgroundType(DEFAULT_IMAGE_1)
			.build();

		OfflineMeeting offlineMeeting4 = OfflineMeeting.builder()
			.offlineMeetingCategory(BEVERAGE)
			.meetingDate(LocalDateTime.now().minusDays(1))
			.meetingStatus(ACTIVE)
			.meetingPlace("서울시 강남구")
			.meetingName("오프라인 모임4")
			.genderRestriction(ALL)
			.backgroundType(DEFAULT_IMAGE_1)
			.build();

		offlineMeetingRepository.save(offlineMeeting1);
		offlineMeetingRepository.save(offlineMeeting2);
		offlineMeetingRepository.save(offlineMeeting3);
		offlineMeetingRepository.save(offlineMeeting4);
	}

	@Test
	@DisplayName("미팅 상태가 ACTIVE이고 미팅 날짜가 현재 날짜 이전인 오프라인 미팅 조회")
	public void findByMeetingStatusAndMeetingDateBefore() {
		// given
		LocalDateTime now = LocalDateTime.now();
		// when
		List<OfflineMeeting> offlineMeetings = offlineMeetingRepository.findByMeetingStatusAndMeetingDateBefore(ACTIVE,
			now);
		// then
		Assertions.assertThat(offlineMeetings.size()).isEqualTo(1);
		Assertions.assertThat(offlineMeetings.get(0).getMeetingName()).isEqualTo("오프라인 모임4");
	}
}
