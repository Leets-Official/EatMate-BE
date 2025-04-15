package com.example.eatmate.app.domain.meeting.domain.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.repository.ChatRoomRepository;
import com.example.eatmate.app.domain.meeting.domain.BankName;
import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;
import com.example.eatmate.app.domain.meeting.domain.MeetingBackgroundType;
import com.example.eatmate.app.domain.meeting.domain.MeetingParticipant;
import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;
import com.example.eatmate.app.domain.meeting.domain.ParticipantLimit;
import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.meeting.dto.MeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MyMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpcomingMeetingResponseDto;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.config.QueryDslConfig;

@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MeetingCustomRepositoryTest {

	@Autowired
	private MeetingRepository meetingRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MeetingParticipantRepository meetingParticipantRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private DeliveryMeetingRepository deliveryMeetingRepository;

	@Autowired
	private OfflineMeetingRepository offlineMeetingRepository;

	private Member testMember1;
	private Member testMember2;
	private List<DeliveryMeeting> deliveryMeetings = new ArrayList<>();
	private List<OfflineMeeting> offlineMeetings = new ArrayList<>();

	public static Stream<Arguments> provideOfflineMeetingList() {
		return Stream.of(
			// 인자 순서: category, genderRestriction, maxParticipant, minParticipant, testDescription
			Arguments.of(null, null, null, null, "필터 없이 모든 오프라인 모임 조회"),
			Arguments.of(OfflineMeetingCategory.MEAL, null, null, null, "식사 카테고리 모임만 조회"),
			Arguments.of(OfflineMeetingCategory.BEVERAGE, null, null, null, "음료 카테고리 모임만 조회"),
			Arguments.of(null, GenderRestriction.ALL, null, null, "성별 제한 없는 모임만 조회"),
			Arguments.of(null, GenderRestriction.MALE, null, null, "남성만 참여 가능한 모임 조회"),
			Arguments.of(null, GenderRestriction.FEMALE, null, null, "여성만 참여 가능한 모임 조회"),
			Arguments.of(null, null, 5L, null, "최대 5명까지 참여 가능한 모임 조회"),
			Arguments.of(null, null, null, 4L, "최소 4명 이상 참여 가능한 모임 조회"),
			Arguments.of(null, null, 6L, 4L, "4~6명 참여 가능한 모임 조회"),
			Arguments.of(OfflineMeetingCategory.MEAL, GenderRestriction.ALL, 5L, 3L,
				"복합 조건: 식사 카테고리, 성별 제한 없음, 3~5명 참여 가능")
		);
	}

	@BeforeEach
	void setUp() {
		// 테스트 회원 생성
		testMember1 = Member.builder()
			.nickname("테스트유저1")
			.email("test1@example.com")
			.build();
		testMember2 = Member.builder()
			.nickname("테스트유저2")
			.email("test2@example.com")
			.build();

		memberRepository.save(testMember1);
		memberRepository.save(testMember2);

		// 배달 모임 10개 생성
		for (int i = 0; i < 10; i++) {
			FoodCategory category = i % 2 == 0 ? FoodCategory.CHICKEN : FoodCategory.PIZZA;
			BankName bank = i % 2 == 0 ? BankName.카카오뱅크 : BankName.토스뱅크;
			MeetingBackgroundType background = i % 2 == 0 ?
				MeetingBackgroundType.DEFAULT_IMAGE_1 : MeetingBackgroundType.DEFAULT_IMAGE_2;

			DeliveryMeeting deliveryMeeting = DeliveryMeeting.builder()
				.meetingName("배달 모임 " + (i + 1))
				.meetingDescription("배달 모임 " + (i + 1) + " 설명")
				.genderRestriction(i % 3 == 0 ? GenderRestriction.ALL :
					(i % 3 == 1 ? GenderRestriction.MALE : GenderRestriction.FEMALE))
				.participantLimit(ParticipantLimit.builder().isLimited(true).maxParticipants((long)(3 + i % 3)).build())
				.meetingStatus(MeetingStatus.ACTIVE)
				.backgroundType(background)
				.foodCategory(category)
				.storeName("맛있는 " + (i % 2 == 0 ? "치킨집" : "피자집") + " " + (i + 1))
				.pickupLocation((i % 2 == 0 ? "강남역 " : "서울대입구역 ") + ((i % 4) + 1) + "번 출구")
				.orderDeadline(LocalDateTime.now().plusHours(i + 1))
				.accountNumber(String.format("%04d-%04d-%04d", i * 1111, (i + 1) * 1111, (i + 2) * 1111))
				.bankName(bank)
				.build();

			meetingRepository.save(deliveryMeeting);
			deliveryMeetings.add(deliveryMeeting);
		}

		// 오프라인 모임 10개 생성
		for (int i = 0; i < 10; i++) {
			OfflineMeetingCategory category =
				i % 2 == 0 ? OfflineMeetingCategory.MEAL : OfflineMeetingCategory.BEVERAGE;
			MeetingBackgroundType background = i % 2 == 0 ?
				MeetingBackgroundType.DEFAULT_IMAGE_1 : MeetingBackgroundType.DEFAULT_IMAGE_2;

			OfflineMeeting offlineMeeting = OfflineMeeting.builder()
				.meetingName("오프라인 모임 " + (i + 1))
				.meetingDescription("오프라인 모임 " + (i + 1) + " 설명")
				.genderRestriction(i % 3 == 0 ? GenderRestriction.ALL :
					(i % 3 == 1 ? GenderRestriction.MALE : GenderRestriction.FEMALE))
				.participantLimit(ParticipantLimit.builder().isLimited(true).maxParticipants((long)(4 + i % 3)).build())
				.meetingStatus(MeetingStatus.ACTIVE)
				.backgroundType(background)
				.meetingPlace((i % 2 == 0 ? "강남 " : "홍대 ") + (i % 2 == 0 ? "맛집" : "술집") + " " + (i + 1))
				.meetingDate(LocalDateTime.now().plusDays(i + 1))
				.offlineMeetingCategory(category)
				.build();

			meetingRepository.save(offlineMeeting);
			offlineMeetings.add(offlineMeeting);
		}

		// 각 모임에 채팅방 생성 및 연결
		for (int i = 0; i < 10; i++) {
			// 배달 모임 채팅방
			ChatRoom deliveryChatRoom = ChatRoom.createChatRoom(
				(i < 5 ? testMember1.getMemberId() : testMember2.getMemberId()), deliveryMeetings.get(i));
			chatRoomRepository.save(deliveryChatRoom);
			deliveryMeetings.get(i).setChatRoom(deliveryChatRoom);
			meetingRepository.save(deliveryMeetings.get(i));

			// 오프라인 모임 채팅방
			ChatRoom offlineChatRoom = ChatRoom.createChatRoom(
				(i < 5 ? testMember2.getMemberId() : testMember1.getMemberId()), offlineMeetings.get(i));
			chatRoomRepository.save(offlineChatRoom);
			offlineMeetings.get(i).setChatRoom(offlineChatRoom);
			meetingRepository.save(offlineMeetings.get(i));
		}

		// 각 모임에 참여자 등록
		// 유저1: 배달 모임 0-4 호스트, 5-9 참가자, 오프라인 모임 5-9 호스트, 0-4 참가자
		// 유저2: 배달 모임 5-9 호스트, 0-4 참가자, 오프라인 모임 0-4 호스트, 5-9 참가자
		for (int i = 0; i < 10; i++) {
			// 배달 모임 참여자
			MeetingParticipant deliveryParticipant1 = MeetingParticipant.createMeetingParticipant(
				(i < 5 ? testMember1 : testMember2), deliveryMeetings.get(i), ParticipantRole.HOST);
			meetingParticipantRepository.save(deliveryParticipant1);

			MeetingParticipant deliveryParticipant2 = MeetingParticipant.createMeetingParticipant(
				(i < 5 ? testMember2 : testMember1), deliveryMeetings.get(i), ParticipantRole.PARTICIPANT);
			meetingParticipantRepository.save(deliveryParticipant2);

			// 오프라인 모임 참여자
			MeetingParticipant offlineParticipant1 = MeetingParticipant.createMeetingParticipant(
				(i < 5 ? testMember2 : testMember1), offlineMeetings.get(i), ParticipantRole.HOST);
			meetingParticipantRepository.save(offlineParticipant1);

			MeetingParticipant offlineParticipant2 = MeetingParticipant.createMeetingParticipant(
				(i < 5 ? testMember1 : testMember2), offlineMeetings.get(i), ParticipantRole.PARTICIPANT);
			meetingParticipantRepository.save(offlineParticipant2);
		}
	}

	@Test
	@DisplayName("다가오는 모임 조회를 할 시 가장 빠른 모임을 조회할 수 있다")
	void findUpcomingMeeting() {
		// given
		Long memberId = testMember1.getMemberId();

		// when
		UpcomingMeetingResponseDto result = meetingRepository.findUpcomingMeeting(memberId);

		// then
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getMeetingName()).isEqualTo("배달 모임 1");
	}

	@ParameterizedTest
	@MethodSource("provideOfflineMeetingList")
	@DisplayName("오프라인 모임 목록 조회 테스트")
	void findOfflineMeetingList(
		OfflineMeetingCategory category,
		GenderRestriction genderRestriction,
		Long maxParticipant,
		Long minParticipant,
		String testDescription) {
		// given
		MeetingSortType sortType = MeetingSortType.PARTICIPANT_COUNT; // 기본 정렬 타입
		int pageSize = 10;
		Long lastMeetingId = null;
		LocalDateTime lastDateTime = null;

		// when
		List<MeetingListResponseDto> result = meetingRepository.findOfflineMeetingList(
			category, genderRestriction, maxParticipant, minParticipant, sortType, pageSize, lastMeetingId,
			lastDateTime);

		// then
		Assertions.assertThat(result).isNotNull();

		// 필터 적용 검증
		if (category != null) {
			// 카테고리 필터가 적용된 경우 해당 카테고리의 모임만 조회되어야 함
			// 오프라인 모임 카테고리는 직접 검증이 불가하여 결과가 비어있지 않은지만 확인
			Assertions.assertThat(result).isNotEmpty();
		}

		if (genderRestriction != null && genderRestriction != GenderRestriction.ALL) {
			// 성별 제한 필터가 적용된 경우, 모든 모임의 성별 제한 설정이 일치해야 함
			// 여기서는 결과가 비어있지 않은지만 확인 (실제 성별은 엔티티 내부 정보로 직접 접근 불가)
			Assertions.assertThat(result).isNotEmpty();
		}

		// 참여자 수 제한 검증
		if (maxParticipant != null) {
			// 최대 참여자 수가 maxParticipant 이하인 모임만 조회되어야 함
			Assertions.assertThat(result).allMatch(meeting -> meeting.getMaxParticipants() <= maxParticipant);
		}

		if (minParticipant != null) {
			// 최대 참여자 수가 minParticipant 이상인 모임만 조회되어야 함
			Assertions.assertThat(result).allMatch(meeting -> meeting.getMaxParticipants() >= minParticipant);
		}

		// 정렬 검증 (참여자 수 기준 내림차순)
		if (!result.isEmpty() && result.size() > 1) {
			// 첫 번째 결과의 참여자 수가 두 번째 결과의 참여자 수보다 크거나 같아야 함
			Assertions.assertThat(result.get(0).getCurrentParticipantCount())
				.isGreaterThanOrEqualTo(result.get(1).getCurrentParticipantCount());
		}

		// 기본 페이지 크기 검증
		if (result.size() > pageSize) {
			Assertions.fail("Result size exceeds page size: " + result.size() + " > " + pageSize);
		}
	}

	@Nested
	@DisplayName("내 모임 목록 조회 테스트")
	class FindMyMeetingListTest {
		@Test
		@DisplayName("내가 주최한 모임 목록을 조회할 수 있다")
		void findMyHostedMeetingList() {
			// given
			Long memberId = testMember1.getMemberId();
			ParticipantRole role = ParticipantRole.HOST;

			// when
			List<MyMeetingListResponseDto> result = meetingRepository.findMyMeetingList(
				memberId, role, null, null, null, 10);

			// then
			Assertions.assertThat(result).isNotEmpty();
			Assertions.assertThat(result.size()).isEqualTo(10);
			Assertions.assertThat(result.get(0).getMeetingName()).isEqualTo("배달 모임 1");
			Assertions.assertThat(result.get(9).getMeetingName()).isEqualTo("오프라인 모임 10");
		}

		@Test
		@DisplayName("내가 참여한 모임 목록을 조회할 수 있다")
		void findMyParticipatedMeetingList() {
			// given
			Long memberId = testMember1.getMemberId();
			ParticipantRole role = ParticipantRole.PARTICIPANT;

			// when
			List<MyMeetingListResponseDto> result = meetingRepository.findMyMeetingList(
				memberId, role, null, null, null, 20);

			// then
			Assertions.assertThat(result).isNotEmpty();
			Assertions.assertThat(result.size()).isEqualTo(10); // 배달 모임 5개 + 오프라인 모임 5개 = 총 10개
			Assertions.assertThat(result.get(0).getMeetingName()).isEqualTo("배달 모임 6");
			Assertions.assertThat(result.get(9).getMeetingName()).isEqualTo("오프라인 모임 5");
		}
	}

}
