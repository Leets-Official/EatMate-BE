package com.example.eatmate.app.domain.meeting.domain.repository;

import static com.example.eatmate.app.domain.meeting.domain.BankName.*;
import static com.example.eatmate.app.domain.meeting.domain.FoodCategory.*;
import static com.example.eatmate.app.domain.meeting.domain.GenderRestriction.*;
import static com.example.eatmate.app.domain.meeting.domain.MeetingBackgroundType.*;
import static com.example.eatmate.app.domain.meeting.domain.MeetingStatus.*;
import static com.example.eatmate.app.domain.meeting.domain.ParticipantRole.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.MeetingParticipant;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MeetingParticipantRepositoryTest {

	@Autowired
	private MeetingParticipantRepository meetingParticipantRepository;

	@Autowired
	private DeliveryMeetingRepository deliveryMeetingRepository;

	@Autowired
	private MemberRepository memberRepository;

	@MockitoBean
	private JPAQueryFactory jpaQueryFactory;

	private DeliveryMeeting deliveryMeeting;
	private Member member1;
	private Member member2;
	private MeetingParticipant meetingParticipant1;
	private MeetingParticipant meetingParticipant2;

	@BeforeEach
	void setUp() {
		deliveryMeeting = DeliveryMeeting.builder()
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

		member1 = Member.builder()
			.nickname("member1")
			.build();

		member2 = Member.builder()
			.nickname("member2")
			.build();

		meetingParticipant1 = MeetingParticipant.builder()
			.meeting(deliveryMeeting)
			.role(HOST)
			.member(member1)
			.build();

		meetingParticipant2 = MeetingParticipant.builder()
			.meeting(deliveryMeeting)
			.role(PARTICIPANT)
			.member(member2)
			.build();

		deliveryMeetingRepository.save(deliveryMeeting);
		memberRepository.save(member1);
		memberRepository.save(member2);
		meetingParticipantRepository.save(meetingParticipant1);
		meetingParticipantRepository.save(meetingParticipant2);
	}

	@Test
	@DisplayName("미팅에 참가중인 인원 조회")
	public void countByMeetingId() {
		// given

		// when
		Long meetingParticipantCount = meetingParticipantRepository.countByMeeting_Id(deliveryMeeting.getId());

		// then
		Assertions.assertThat(meetingParticipantCount).isEqualTo(2);
	}

	@Test
	@DisplayName("미팅에 참가중인 참여자 역할 회원 조회")
	public void findByMeetingAndRole() {
		// given

		// when
		Optional<MeetingParticipant> meetingParticipants = meetingParticipantRepository.findByMeetingAndRole(
			deliveryMeeting, PARTICIPANT);

		// then
		Assertions.assertThat(meetingParticipants)
			.get()
			.extracting(MeetingParticipant::getRole)
			.isEqualTo(PARTICIPANT);
	}

	@Test
	@DisplayName("미팅에 참가중인 호스트 역할 회원 조회")
	public void findByMeetingAndRole2() {
		// given

		// when
		Optional<MeetingParticipant> meetingParticipants = meetingParticipantRepository.findByMeetingAndRole(
			deliveryMeeting, HOST);

		// then
		Assertions.assertThat(meetingParticipants)
			.get()
			.extracting(MeetingParticipant::getRole)
			.isEqualTo(HOST);
	}

	@Test
	@DisplayName("회원이 참여중인 미팅 조회")
	public void findByMeetingAndMember() {
		// given

		// when
		Optional<MeetingParticipant> meetingParticipants = meetingParticipantRepository.findByMeetingAndMember(
			deliveryMeeting, member1);

		// then
		Assertions.assertThat(meetingParticipants)
			.get()
			.extracting(MeetingParticipant::getMember)
			.isEqualTo(member1);
	}

	@Test
	@DisplayName("미팅에 참가중인 회원이 존재하는지 확인")
	public void existsByMeetingAndMember() {
		// given

		// when
		boolean exists = meetingParticipantRepository.existsByMeetingAndMember(deliveryMeeting, member1);

		// then
		Assertions.assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("미팅에 HOST가 아닌 멤버 수 확인")
	public void countByMeetingAndRoleNot() {
		// given

		// when
		Long count = meetingParticipantRepository.countByMeetingAndRoleNot(deliveryMeeting, HOST);

		// then
		Assertions.assertThat(count).isEqualTo(1);
	}

	@Test
	@DisplayName("미팅에 참여중인 회원 목록 확인")
	public void findByMeeting() {
		// given

		// when
		List<MeetingParticipant> meetingParticipants = meetingParticipantRepository.findByMeeting(deliveryMeeting);

		// then
		Assertions.assertThat(meetingParticipants.size()).isEqualTo(2);
	}

}
