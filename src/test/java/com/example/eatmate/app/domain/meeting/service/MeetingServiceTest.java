package com.example.eatmate.app.domain.meeting.service;

import static com.example.eatmate.app.domain.meeting.domain.BankName.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.repository.ChatRoomRepository;
import com.example.eatmate.app.domain.chatRoom.domain.repository.MemberChatRoomRepository;
import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;
import com.example.eatmate.app.domain.meeting.domain.MeetingParticipant;
import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.ParticipantLimit;
import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.meeting.domain.repository.DeliveryMeetingRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingParticipantRepository;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingRepository;
import com.example.eatmate.app.domain.member.domain.Gender;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class MeetingServiceTest {

	@Autowired
	private MeetingService meetingService;

	@Autowired
	private DeliveryMeetingRepository deliveryMeetingRepository;

	@Autowired
	private MeetingParticipantRepository meetingParticipantRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	private DeliveryMeeting testMeeting;
	private Member hostMember;    // 호스트 멤버
	private Member member2;       // 참여 시도할 첫 번째 멤버
	private Member member3;  // 참여 시도할 두 번째 멤버
	private UserDetails member2Details;  // 첫 번째 멤버의 UserDetails
	private UserDetails member3Details;  // 두 번째 멤버의 UserDetails
	@Autowired
	private MemberChatRoomRepository memberChatRoomRepository;
	@Autowired
	private MeetingRepository meetingRepository;

	@BeforeEach
	void setUp() {
		// 테스트 미팅 생성 (2인 제한)
		testMeeting = DeliveryMeeting.builder()
			.meetingName("Test Meeting")
			.meetingDescription("Test Description")
			.genderRestriction(GenderRestriction.ALL)
			.participantLimit(ParticipantLimit.builder()
				.isLimited(true)
				.maxParticipants(2L)
				.build())
			.foodCategory(FoodCategory.CHICKEN)
			.storeName("Test Store")
			.meetingStatus(MeetingStatus.ACTIVE)
			.bankName(국민은행)
			.pickupLocation("Test Location")
			.orderDeadline(LocalDateTime.now().plusHours(1))
			.accountNumber("1234-5678")
			.build();

		// 호스트 멤버 생성
		hostMember = Member.builder()
			.email("host@test.com")
			.nickname("host")
			.gender(Gender.MALE)
			.build();

		// 첫 번째 참여 시도 멤버 생성
		member2 = Member.builder()
			.email("test2@test.com")
			.nickname("tester2")
			.gender(Gender.MALE)
			.build();

		// 두 번째 참여 시도 멤버 생성
		member3 = Member.builder()
			.email("test3@test.com")
			.nickname("tester3")
			.gender(Gender.MALE)
			.build();

		member2Details = User.withUsername(member2.getEmail())
			.password("password")
			.roles("USER")
			.build();

		member3Details = User.withUsername(member3.getEmail())
			.password("password")
			.roles("USER")
			.build();

		// 데이터 저장
		memberRepository.saveAndFlush(hostMember);
		memberRepository.saveAndFlush(member2);
		memberRepository.saveAndFlush(member3);
		deliveryMeetingRepository.saveAndFlush(testMeeting);

		// 채팅방 생성
		ChatRoom chatRoom = ChatRoom.createChatRoom(hostMember.getMemberId(), testMeeting);
		chatRoomRepository.save(chatRoom);

		// Meeting에 채팅방 연결
		testMeeting.setChatRoom(chatRoom);
		deliveryMeetingRepository.save(testMeeting);

		// 호스트를 참가자로 등록
		MeetingParticipant hostParticipant = MeetingParticipant.createMeetingParticipant(
			hostMember, testMeeting, ParticipantRole.HOST);
		meetingParticipantRepository.save(hostParticipant);

	}

	@AfterEach
	void tearDown() {
		// 테스트 종료 후 데이터 초기화
		meetingParticipantRepository.deleteAll();
		deliveryMeetingRepository.deleteAll();
		meetingRepository.deleteAll();
		memberChatRoomRepository.deleteAll();
		chatRoomRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("동시에 마지막 자리 참여 시도 테스트")
	void concurrentJoinTest() throws InterruptedException {

		// 동시에 실행할 스레드 개수 설정 (2개: member2, member3의 요청)
		int numberOfThreads = 2;

		// 고정된 크기의 스레드 풀 생성 (2개의 스레드를 동시에 실행)
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

		// 두 스레드의 작업 완료를 기다리기 위한 CountDownLatch 생성
		// 각 스레드가 작업을 마치면 latch.countDown()을 호출하여 카운트를 감소
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		// 스레드 안전한 카운터 변수 생성
		// 여러 스레드에서 동시에 접근해도 안전하게 값을 증가시킬 수 있음
		AtomicInteger successCount = new AtomicInteger(0); // 참여 성공 횟수
		AtomicInteger failCount = new AtomicInteger(0);    // 참여 실패 횟수

		// member2의 참여 요청을 스레드 풀에 제출
		executorService.submit(() -> {
			try {
				// member2가 모임 참여 시도
				meetingService.joinDeliveryMeeting(testMeeting.getId(), member2Details);
				// 참여 성공시 successCount 증가
				successCount.incrementAndGet();
			} catch (Exception e) {
				// 인원 초과로 실패시 failCount 증가
				if (e instanceof CommonException &&
					((CommonException)e).getErrorCode() == ErrorCode.PARTICIPANT_LIMIT_EXCEEDED) {
					failCount.incrementAndGet();
				}
			} finally {
				// 작업 완료 후 CountDownLatch 카운트 감소
				latch.countDown();
			}
		});

		// member3의 참여 요청을 스레드 풀에 제출 (member2와 동일한 로직)
		executorService.submit(() -> {
			try {
				meetingService.joinDeliveryMeeting(testMeeting.getId(), member3Details);
				successCount.incrementAndGet();
			} catch (CommonException e) {
				if (e.getErrorCode() == ErrorCode.PARTICIPANT_LIMIT_EXCEEDED) {
					failCount.incrementAndGet();
				}
			} finally {
				latch.countDown();
			}
		});

		// CountDownLatch가 0이 될 때까지 대기 (두 스레드의 작업이 모두 완료될 때까지 대기)
		latch.await();

		// 더 이상 필요없는 스레드 풀 종료
		executorService.shutdown();

		// 성공/실패 카운트 로깅
		log.info("성공 참가 횟수 - 기대값: {}, 실제값: {}", 1, successCount.get());
		log.info("실패 참가 횟수 - 기대값: {}, 실제값: {}", 1, failCount.get());
		assertEquals(1, successCount.get(), "성공적인 참가는 1회여야 합니다");
		assertEquals(1, failCount.get(), "참가 실패는 1회여야 합니다");

		// 최종 참가자 수 로깅
		Long finalParticipantCount = meetingParticipantRepository.countByMeeting_Id(testMeeting.getId());
		log.info("최종 참가자 수 - 기대값: {}, 실제값: {}", 2, finalParticipantCount);
		assertEquals(2, finalParticipantCount, "최종 참가자 수는 2명이어야 합니다 (호스트 1명 + 성공한 참가자 1명)");
	}
}
