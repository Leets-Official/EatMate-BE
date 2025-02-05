package com.example.eatmate.app.domain.meeting.domain.repository;

import static com.example.eatmate.app.domain.meeting.domain.QDeliveryMeeting.*;
import static com.example.eatmate.app.domain.meeting.domain.QMeeting.*;
import static com.example.eatmate.app.domain.meeting.domain.QMeetingParticipant.*;
import static com.example.eatmate.app.domain.meeting.domain.QOfflineMeeting.*;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;
import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;
import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.meeting.dto.MeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.MyMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpcomingMeetingResponseDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MeetingCustomRepositoryImpl implements MeetingCustomRepository {
	private final JPAQueryFactory queryFactory;

	/**
	 * 사용자의 모임 목록을 조회하는 메서드
	 *
	 * @param memberId 조회할 회원 ID
	 * @param role 조회할 역할 (HOST/PARTICIPANT)
	 * @param meetingStatus 모임 상태 (ACTIVE/INACTIVE)
	 * @param lastMeetingId 마지막으로 조회한 모임 ID (페이징)
	 * @param lastDateTime 마지막으로 조회한 시간 (페이징)
	 * @param pageSize 페이지 크기
	 * @return 모임 목록
	 */
	@Override
	public List<MyMeetingListResponseDto> findMyMeetingList(Long memberId, ParticipantRole role,
		MeetingStatus meetingStatus, Long lastMeetingId, LocalDateTime lastDateTime, int pageSize) {

		// 배달 모임인지 여부를 확인하는 조건
		BooleanExpression isDelivery = meeting.type.eq("DELIVERY");

		// 모임 상태와 역할에 대한 필터링 조건
		BooleanExpression statusCondition = meetingStatus != null ?
			meeting.meetingStatus.eq(meetingStatus) : null;
		BooleanExpression roleCondition = role != null ?
			meetingParticipant.role.eq(role) : null;

		// 모임 시간을 가져오는 표현식
		// 배달 모임이면 주문 마감시간, 아니면 모임 시간을 가져옴
		DateTimeExpression<LocalDateTime> meetingTimeExpr = new CaseBuilder()
			.when(meeting.type.eq("DELIVERY"))
			.then(deliveryMeeting.orderDeadline)
			.otherwise(offlineMeeting.meetingDate);

		// No-Offset 페이징을 위한 동적 조건 생성
		BooleanExpression cursorCondition = getCursorCondition(lastMeetingId, lastDateTime, meetingTimeExpr);

		return queryFactory
			.select(Projections.constructor(MyMeetingListResponseDto.class,
				meeting.type,                    // 모임 타입 (DELIVERY/OFFLINE)
				meeting.id,                      // 모임 ID
				meeting.meetingName,             // 모임 이름
				meeting.meetingStatus,           // 모임 상태
				meeting.meetingDescription,      // 모임 설명
				meeting.participantLimit.maxParticipants,  // 최대 참여자 수

				// 오프라인 모임 카테고리 (배달 모임인 경우 null)
				ExpressionUtils.as(
					new CaseBuilder()
						.when(isDelivery)
						.then(Expressions.nullExpression(OfflineMeetingCategory.class))
						.otherwise(JPAExpressions
							.select(offlineMeeting.offlineMeetingCategory)
							.from(offlineMeeting)
							.where(offlineMeeting.id.eq(meeting.id))),
					"offlineMeetingCategory"
				),

				meeting.createdAt,   // 생성 시간

				// 모임 장소 (배달 모임: 가게 이름, 오프라인 모임: 모임 장소)
				ExpressionUtils.as(
					new CaseBuilder()
						.when(isDelivery)
						.then(JPAExpressions
							.select(deliveryMeeting.storeName)
							.from(deliveryMeeting)
							.where(deliveryMeeting.id.eq(meeting.id)))
						.otherwise(JPAExpressions
							.select(offlineMeeting.meetingPlace)
							.from(offlineMeeting)
							.where(offlineMeeting.id.eq(meeting.id))),
					"location"),

				// 마감 시간 (배달 모임: 주문 마감 시간, 오프라인 모임: 모임 시간)
				ExpressionUtils.as(
					new CaseBuilder()
						.when(isDelivery)
						.then(JPAExpressions
							.select(deliveryMeeting.orderDeadline)
							.from(deliveryMeeting)
							.where(deliveryMeeting.id.eq(meeting.id)))
						.otherwise(JPAExpressions
							.select(offlineMeeting.meetingDate)
							.from(offlineMeeting)
							.where(offlineMeeting.id.eq(meeting.id))),
					"dueDateTime"),

				// 현재 참여자 수 계산
				ExpressionUtils.as(
					JPAExpressions
						.select(meetingParticipant.count())
						.from(meetingParticipant)
						.where(meetingParticipant.meeting.id.eq(meeting.id)),
					"participantCount"),
				meeting.chatRoom.lastChatAt
			))
			.from(meeting)
			// 배달/오프라인 모임 테이블과 left join
			.leftJoin(deliveryMeeting).on(deliveryMeeting.id.eq(meeting.id))
			.leftJoin(offlineMeeting).on(offlineMeeting.id.eq(meeting.id))
			// 참여자 정보와 join
			.join(meetingParticipant).on(
				meetingParticipant.meeting.id.eq(meeting.id),
				meetingParticipant.member.memberId.eq(memberId)
			)
			// 조건절 적용
			.where(statusCondition, roleCondition, cursorCondition)
			// 정렬 조건:
			// 1. 모임 상태 오름차순 (활성 모임 우선)
			// 2. 모임 시간 기준 오름차순
			// 3. 모임 ID 내림차순
			.orderBy(
				meeting.meetingStatus.asc(),
				new OrderSpecifier<>(Order.ASC,
					new CaseBuilder()
						.when(isDelivery)
						.then(JPAExpressions
							.select(deliveryMeeting.orderDeadline)
							.from(deliveryMeeting)
							.where(deliveryMeeting.id.eq(meeting.id)))
						.otherwise(JPAExpressions
							.select(offlineMeeting.meetingDate)
							.from(offlineMeeting)
							.where(offlineMeeting.id.eq(meeting.id)))
				),
				meeting.id.desc()
			)
			.limit(pageSize + 1)  // 다음 페이지 존재 여부 확인을 위해 +1
			.fetch();
	}

	/**
	 * No-Offset 페이징을 위한 커서 조건 생성
	 * 마지막으로 조회한 모임 이후의 데이터만 조회하도록 함
	 */
	private BooleanExpression getCursorCondition(Long lastMeetingId, LocalDateTime lastDateTime,
		DateTimeExpression<LocalDateTime> meetingTimeExpr) {

		if (lastMeetingId == null || lastDateTime == null) {
			return null;
		}

		// 활성 상태인 모임만 필터링하고
		// 마지막으로 조회한 시간 이후의 모임 또는
		// 같은 시간대의 모임 중 ID가 더 작은 모임을 조회
		return new CaseBuilder()
			.when(meeting.meetingStatus.ne(MeetingStatus.INACTIVE))
			.then(1)
			.otherwise(0)
			.eq(new CaseBuilder()
				.when(meeting.meetingStatus.ne(MeetingStatus.INACTIVE))
				.then(1)
				.otherwise(0))
			.and(meetingTimeExpr.gt(lastDateTime)
				.or(meetingTimeExpr.eq(lastDateTime)
					.and(meeting.id.lt(lastMeetingId))));
	}

	/**
	 * 사용자의 다가오는 가장 가까운 모임 조회
	 */
	@Override
	public UpcomingMeetingResponseDto findUpcomingMeeting(Long memberId) {
		BooleanExpression isDelivery = meeting.type.eq("DELIVERY");

		return queryFactory
			.select(Projections.constructor(UpcomingMeetingResponseDto.class,
				meetingParticipant.member.nickname,
				// 모임 시간 (배달/오프라인 구분하여 조회)
				ExpressionUtils.as(
					new CaseBuilder()
						.when(isDelivery)
						.then(JPAExpressions
							.select(deliveryMeeting.orderDeadline)
							.from(deliveryMeeting)
							.where(deliveryMeeting.id.eq(meeting.id)))
						.otherwise(JPAExpressions
							.select(offlineMeeting.meetingDate)
							.from(offlineMeeting)
							.where(offlineMeeting.id.eq(meeting.id))),
					"meetingTime"),
				// 모임 장소 (배달: 가게이름, 오프라인: 모임장소)
				ExpressionUtils.as(
					new CaseBuilder()
						.when(isDelivery)
						.then(JPAExpressions
							.select(deliveryMeeting.storeName)
							.from(deliveryMeeting)
							.where(deliveryMeeting.id.eq(meeting.id)))
						.otherwise(JPAExpressions
							.select(offlineMeeting.meetingPlace)
							.from(offlineMeeting)
							.where(offlineMeeting.id.eq(meeting.id))),
					"meetingLocation"),
				JPAExpressions
					.select(offlineMeeting.offlineMeetingCategory)
					.from(offlineMeeting)
					.where(offlineMeeting.id.eq(meeting.id)),
				meeting.type
			))
			.from(meeting)
			.join(meetingParticipant).on(
				meetingParticipant.meeting.id.eq(meeting.id),
				meetingParticipant.member.memberId.eq(memberId)
			)
			// 활성 상태인 모임만 조회
			.where(meeting.meetingStatus.eq(MeetingStatus.ACTIVE))
			// 가장 가까운 시간 순으로 정렬
			.orderBy(
				new OrderSpecifier<>(Order.ASC,
					new CaseBuilder()
						.when(isDelivery)
						.then(JPAExpressions
							.select(deliveryMeeting.orderDeadline)
							.from(deliveryMeeting)
							.where(deliveryMeeting.id.eq(meeting.id)))
						.otherwise(JPAExpressions
							.select(offlineMeeting.meetingDate)
							.from(offlineMeeting)
							.where(offlineMeeting.id.eq(meeting.id)))
				)
			)
			.fetchFirst();  // 첫 번째 결과만 조회
	}

	/**
	 * 배달 모임 목록 조회
	 *
	 * @param category 음식 카테고리 필터
	 * @param genderRestriction 성별 제한 필터
	 * @param maxParticipant 최대 참여자 수 상한 필터
	 * @param minParticipant 최대 참여자 수 하한 필터
	 * @param sortType 정렬 방식 (생성일/모임시간/참여자수)
	 * @param pageSize 페이지 크기
	 * @param lastMeetingId 마지막 조회 모임 ID (페이징)
	 * @param lastDateTime 마지막 조회 시간 (페이징)
	 */
	@Override
	public List<MeetingListResponseDto> findDeliveryMeetingList(FoodCategory category,
		GenderRestriction genderRestriction, Long maxParticipant, Long minParticipant, MeetingSortType sortType,
		int pageSize, Long lastMeetingId, LocalDateTime lastDateTime) {

		// 음식 카테고리 필터링 조건
		BooleanExpression isCategory = category != null ?
			deliveryMeeting.foodCategory.eq(category) : null;

		// 모임 시간 표현식 (배달 모임은 주문 마감시간 기준)
		DateTimeExpression<LocalDateTime> meetingTimeExpr = deliveryMeeting.orderDeadline;

		// 공통 모임 목록 조회 메서드 호출
		return findMeetingList(
			isCategory,                  // 카테고리 조건
			genderRestriction,           // 성별 제한 조건
			maxParticipant,             // 최대 참여자 수 상한
			minParticipant,             // 최대 참여자 수 하한
			sortType,                    // 정렬 방식
			pageSize,                    // 페이지 크기
			lastMeetingId,              // 마지막 모임 ID
			lastDateTime,               // 마지막 조회 시간
			meetingTimeExpr,            // 모임 시간 표현식
			createDeliveryMeetingProjection(),  // 배달 모임용 프로젝션
			deliveryMeeting,            // 조인할 테이블
			deliveryMeeting.id          // 조인 키
		);
	}

	/**
	 * 오프라인 모임 목록 조회
	 * (매개변수 설명은 배달 모임과 동일)
	 */
	@Override
	public List<MeetingListResponseDto> findOfflineMeetingList(
		OfflineMeetingCategory category,
		GenderRestriction genderRestriction,
		Long maxParticipant,
		Long minParticipant,
		MeetingSortType sortType,
		int pageSize,
		Long lastMeetingId,
		LocalDateTime lastDateTime) {

		// 오프라인 모임 카테고리 필터링 조건
		BooleanExpression isCategory = category != null ?
			offlineMeeting.offlineMeetingCategory.eq(category) : null;

		// 모임 시간 표현식 (오프라인 모임은 모임 시간 기준)
		DateTimeExpression<LocalDateTime> meetingTimeExpr = offlineMeeting.meetingDate;

		// 공통 모임 목록 조회 메서드 호출
		return findMeetingList(
			isCategory,
			genderRestriction,
			maxParticipant,
			minParticipant,
			sortType,
			pageSize,
			lastMeetingId,
			lastDateTime,
			meetingTimeExpr,
			createOfflineMeetingProjection(),  // 오프라인 모임용 프로젝션
			offlineMeeting,
			offlineMeeting.id
		);
	}

	/**
	 * 모임 목록 조회를 위한 공통 메서드
	 * 배달 모임과 오프라인 모임 조회에 공통으로 사용되는 로직
	 *
	 * @param <T> 조회할 모임의 타입 (DeliveryMeeting 또는 OfflineMeeting)
	 * @param categoryCondition 카테고리 필터링 조건 (음식/모임 카테고리)
	 * @param genderRestriction 성별 제한 필터 (남자만/여자만/제한없음)
	 * @param maxParticipant 최대 참여자 수 상한값
	 * @param minParticipant 최대 참여자 수 하한값
	 * @param sortType 정렬 기준 (생성일/모임시간/참여자수)
	 * @param pageSize 페이지 크기
	 * @param lastMeetingId 마지막으로 조회된 모임 ID (페이징)
	 * @param lastDateTime 마지막으로 조회된 시간 (페이징)
	 * @param meetingTimeExpr 모임 시간 표현식 (배달: 주문마감시간, 오프라인: 모임시간)
	 * @param projection 조회할 필드 정의
	 * @param joinTable 조인할 테이블 (배달/오프라인 모임 테이블)
	 * @param joinTableId 조인 키
	 */
	private <T> List<MeetingListResponseDto> findMeetingList(
		BooleanExpression categoryCondition,
		GenderRestriction genderRestriction,
		Long maxParticipant,
		Long minParticipant,
		MeetingSortType sortType,
		int pageSize,
		Long lastMeetingId,
		LocalDateTime lastDateTime,
		DateTimeExpression<LocalDateTime> meetingTimeExpr,
		ConstructorExpression<MeetingListResponseDto> projection,
		EntityPathBase<T> joinTable,
		NumberPath<Long> joinTableId) {

		// 성별 제한 조건
		BooleanExpression genderCondition = createGenderCondition(genderRestriction);

		// 모임 현재 참여인원
		NumberExpression<Long> participantCount = calculateParticipantCount();

		// 모임 최대 인원 조건
		BooleanExpression participantCondition = createParticipantCondition(maxParticipant, minParticipant);

		// No-offset 페이지네이션을 위한 동적 조건 추가
		BooleanExpression cursorCondition = createCursorCondition(lastMeetingId, lastDateTime, sortType,
			meetingTimeExpr);

		return queryFactory
			.select(projection)           // DTO 변환을 위한 필드 선택
			.from(meeting)               // 기본 모임 테이블
			.join(joinTable)             // 배달/오프라인 모임 테이블과 조인
			.on(joinTableId.eq(meeting.id))
			.where(
				meeting.meetingStatus.eq(MeetingStatus.ACTIVE),  // 활성화된 모임만 조회
				categoryCondition,        // 카테고리 필터 적용
				genderCondition,         // 성별 제한 필터 적용
				participantCondition,     // 참여자 수 제한 필터 적용
				cursorCondition          // 페이징 조건 적용
			)
			.orderBy(createOrderSpecifier(sortType, participantCount, meetingTimeExpr))  // 정렬 조건 적용
			.limit(pageSize + 1)         // No-Offset 페이징을 위해 limit + 1
			.fetch();                    // 결과 조회
	}

	/**
	 * 배달 모임 조회를 위한 프로젝션 생성
	 * MeetingListResponseDto에 매핑될 필드들을 정의
	 * 배달 모임의 특성에 맞는 필드 매핑(예: 가게이름, 주문마감시간 등)
	 */
	private ConstructorExpression<MeetingListResponseDto> createDeliveryMeetingProjection() {
		return Projections.constructor(MeetingListResponseDto.class,
			meeting.id,                     // 모임 ID
			meeting.meetingName,            // 모임 이름
			meeting.meetingDescription,     // 모임 설명
			calculateParticipantCount(),    // 현재 참여자 수 계산
			meeting.participantLimit.maxParticipants,  // 최대 참여 가능 인원
			deliveryMeeting.storeName.as("location"),  // 가게 이름을 location으로 매핑
			meeting.createdAt,              // 모임 생성 시간
			deliveryMeeting.orderDeadline.as("dueDateTime"),  // 주문 마감시간을 dueDateTime으로 매핑
			meeting.chatRoom.lastChatAt     // 마지막 채팅 시간
		);
	}

	/**
	 * 오프라인 모임 조회를 위한 프로젝션 생성
	 * MeetingListResponseDto에 매핑될 필드들을 정의
	 * 오프라인 모임의 특성에 맞는 필드 매핑(예: 모임장소, 모임시간 등)
	 */
	private ConstructorExpression<MeetingListResponseDto> createOfflineMeetingProjection() {
		return Projections.constructor(MeetingListResponseDto.class,
			meeting.id,                     // 모임 ID
			meeting.meetingName,            // 모임 이름
			meeting.meetingDescription,     // 모임 설명
			calculateParticipantCount(),    // 현재 참여자 수 계산
			meeting.participantLimit.maxParticipants,  // 최대 참여 가능 인원
			offlineMeeting.meetingPlace.as("location"),  // 모임 장소를 location으로 매핑
			meeting.createdAt,              // 모임 생성 시간
			offlineMeeting.meetingDate.as("dueDateTime"),  // 모임 시간을 dueDateTime으로 매핑
			meeting.chatRoom.lastChatAt     // 마지막 채팅 시간
		);
	}

	/**
	 * 모임의 현재 참여자 수를 계산하는 서브쿼리 표현식 생성
	 * meetingParticipant 테이블에서 해당 모임의 참여자 수를 카운트
	 */
	private NumberExpression<Long> calculateParticipantCount() {
		return Expressions.asNumber(
			JPAExpressions
				.select(meetingParticipant.count())  // 참여자 수 카운트
				.from(meetingParticipant)            // 참여자 테이블
				.where(meetingParticipant.meeting.eq(meeting))  // 현재 모임의 참여자만 필터링
		).castToNum(Long.class);
	}

	/**
	 * 성별 제한 조건 생성
	 * 성별 제한이 있는 경우 해당 조건을 반환하고, 없으면 null 반환
	 */
	private BooleanExpression createGenderCondition(GenderRestriction genderRestriction) {
		return genderRestriction != null ?
			meeting.genderRestriction.eq(genderRestriction) : null;
	}

	/**
	 * 참여자 수 제한 조건 생성
	 * 최대 참여자 수의 상한과 하한을 기준으로 필터링 조건 생성
	 *
	 * @param maxParticipant 최대 참여자 수 상한값
	 * @param minParticipant 최대 참여자 수 하한값
	 * @return 참여자 수 필터링 조건
	 */
	private BooleanExpression createParticipantCondition(Long maxParticipant, Long minParticipant) {
		if (maxParticipant == null && minParticipant == null) {
			return null;
		}

		// 1. 인원 제한이 있는 모임만 먼저 필터링
		BooleanExpression condition = meeting.participantLimit.isLimited.isTrue();

		// 2. 최대 인원 상한 조건
		if (maxParticipant != null) {
			condition = condition.and(meeting.participantLimit.maxParticipants.loe(maxParticipant));
		}

		// 3. 최대 인원 하한 조건
		if (minParticipant != null) {
			condition = condition.and(meeting.participantLimit.maxParticipants.goe(minParticipant));
		}

		return condition;
	}

	/**
	 * 정렬 조건에 따른 커서 기반 페이징 조건 생성
	 * No-Offset 페이징을 위한 동적 조건 생성
	 */
	private BooleanExpression createCursorCondition(Long lastMeetingId, LocalDateTime lastDateTime,
		MeetingSortType sortType, DateTimeExpression<LocalDateTime> meetingTimeExpr) {
		if (lastMeetingId == null || lastDateTime == null) {
			return null;
		}

		// 정렬 타입에 따른 커서 조건 생성
		switch (sortType) {
			case CREATED_AT:    // 생성일 기준 정렬
				return meeting.createdAt.lt(lastDateTime)
					.or(meeting.createdAt.eq(lastDateTime)
						.and(meeting.id.lt(lastMeetingId)));

			case MEETING_TIME:  // 모임 시간 기준 정렬
				return meetingTimeExpr.gt(lastDateTime)
					.or(meetingTimeExpr.eq(lastDateTime)
						.and(meeting.id.gt(lastMeetingId)));

			case PARTICIPANT_COUNT:  // 참여자 수 기준 정렬
				NumberExpression<Long> participantCount = calculateParticipantCount();
				NumberExpression<Long> lastParticipantCount = Expressions.asNumber(
					JPAExpressions
						.select(meetingParticipant.count())
						.from(meetingParticipant)
						.where(meetingParticipant.meeting.id.eq(lastMeetingId))
				).castToNum(Long.class);

				// 참여자 수가 같으면 ID로 보조 정렬
				return participantCount.lt(lastParticipantCount)
					.or(participantCount.eq(lastParticipantCount)
						.and(meeting.id.lt(lastMeetingId)));

			default:
				return null;
		}
	}

	/**
	 * 정렬 조건을 생성하는 메서드
	 * 정렬 타입에 따라 적절한 정렬 조건을 생성
	 *
	 * @param sortType 정렬 기준 (생성일/모임시간/참여자수)
	 * @param participantCount 참여자 수 표현식
	 * @param meetingTimeExpr 모임 시간 표현식
	 * @return 정렬 조건
	 */
	private OrderSpecifier<?> createOrderSpecifier(
		MeetingSortType sortType,
		NumberExpression<Long> participantCount,
		Expression<LocalDateTime> meetingTimeExpr) {

		if (sortType == null) {
			return participantCount.desc();  // 기본값: 참여자 수 내림차순
		}

		switch (sortType) {
			case CREATED_AT:         // 생성일 기준 내림차순
				return meeting.createdAt.desc();
			case MEETING_TIME:       // 모임 시간 기준 오름차순
				return new OrderSpecifier<>(Order.ASC, meetingTimeExpr);
			case PARTICIPANT_COUNT:  // 참여자 수 기준 내림차순
				return participantCount.desc();
			default:
				return participantCount.desc();  // 기본값: 참여자 수 내림차순
		}
	}
}
