package com.example.eatmate.app.domain.meeting.domain.repository;

import static com.example.eatmate.app.domain.meeting.domain.BankName.*;
import static com.example.eatmate.app.domain.meeting.domain.FoodCategory.*;
import static com.example.eatmate.app.domain.meeting.domain.GenderRestriction.*;
import static com.example.eatmate.app.domain.meeting.domain.MeetingBackgroundType.*;
import static com.example.eatmate.app.domain.meeting.domain.MeetingStatus.*;

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

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.querydsl.jpa.impl.JPAQueryFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeliveryMeetingRepositoryTest {

	@Autowired
	private DeliveryMeetingRepository deliveryMeetingRepository;

	@MockitoBean
	private JPAQueryFactory jpaQueryFactory;

	@BeforeEach
	void setUp() {
		DeliveryMeeting deliveryMeeting1 = DeliveryMeeting.builder()
			.meetingName("배달 모임1")
			.meetingStatus(ACTIVE)
			.orderDeadline(LocalDateTime.now().plusDays(1))
			.backgroundType(DEFAULT_IMAGE_1)
			.genderRestriction(ALL)
			.accountNumber("123-123-123")
			.bankName(국민은행)
			.foodCategory(KOREAN)
			.pickupLocation("서울시 강남구")
			.storeName("맛집")
			.build();

		DeliveryMeeting deliveryMeeting2 = DeliveryMeeting.builder()
			.meetingName("배달 모임2")
			.meetingStatus(INACTIVE)
			.orderDeadline(LocalDateTime.now().plusDays(1))
			.backgroundType(DEFAULT_IMAGE_1)
			.genderRestriction(ALL)
			.accountNumber("123-123-123")
			.bankName(국민은행)
			.foodCategory(KOREAN)
			.storeName("맛집")
			.pickupLocation("서울시 강남구")
			.build();

		DeliveryMeeting deliveryMeeting3 = DeliveryMeeting.builder()
			.meetingName("배달 모임3")
			.meetingStatus(INACTIVE)
			.orderDeadline(LocalDateTime.now().minusDays(1))
			.backgroundType(DEFAULT_IMAGE_1)
			.genderRestriction(ALL)
			.accountNumber("123-123-123")
			.foodCategory(KOREAN)
			.bankName(국민은행)
			.storeName("맛집")
			.pickupLocation("서울시 강남구")
			.build();

		DeliveryMeeting deliveryMeeting4 = DeliveryMeeting.builder()
			.meetingName("배달 모임4")
			.meetingStatus(ACTIVE)
			.orderDeadline(LocalDateTime.now().minusDays(1))
			.backgroundType(DEFAULT_IMAGE_1)
			.genderRestriction(ALL)
			.accountNumber("123-123-123")
			.foodCategory(KOREAN)
			.bankName(국민은행)
			.storeName("맛집")
			.pickupLocation("서울시 강남구")
			.build();

		deliveryMeetingRepository.save(deliveryMeeting1);
		deliveryMeetingRepository.save(deliveryMeeting2);
		deliveryMeetingRepository.save(deliveryMeeting3);
		deliveryMeetingRepository.save(deliveryMeeting4);

	}

	@Test
	@DisplayName("미팅 상태가 ACTIVE이고 미팅 날짜가 현재 날짜 이전인 배달 미팅 조회")
	public void findByMeetingStatusAndMeetingDateBefore() {
		// given
		LocalDateTime now = LocalDateTime.now();
		// when
		List<DeliveryMeeting> offlineMeetings = deliveryMeetingRepository.findByMeetingStatusAndOrderDeadlineBefore(
			ACTIVE,
			now);
		// then
		Assertions.assertThat(offlineMeetings.size()).isEqualTo(1);
		Assertions.assertThat(offlineMeetings.get(0).getMeetingName()).isEqualTo("배달 모임4");
	}
}
