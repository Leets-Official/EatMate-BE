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

	@Override
	public List<MyMeetingListResponseDto> findMyMeetingList(Long memberId, ParticipantRole role,
		MeetingStatus meetingStatus, Long lastMeetingId, LocalDateTime lastDateTime, int pageSize) {

		BooleanExpression isDelivery = meeting.type.eq("DELIVERY");

		// 상태와 역할에 대한 조건
		BooleanExpression statusCondition = meetingStatus != null ?
			meeting.meetingStatus.eq(meetingStatus) : null;

		BooleanExpression roleCondition = role != null ?
			meetingParticipant.role.eq(role) : null;

		// No-Offset 페이징을 위한 동적 조건
		BooleanExpression cursorCondition = getCursorCondition(lastMeetingId, lastDateTime);

		return queryFactory
			.select(Projections.constructor(MyMeetingListResponseDto.class,
				meeting.type,
				meeting.id,
				meeting.meetingName,
				meeting.meetingStatus,
				ExpressionUtils.as(
					new CaseBuilder()
						.when(meeting.type.eq("DELIVERY"))
						.then(JPAExpressions
							.select(deliveryMeeting.storeName)
							.from(deliveryMeeting)
							.where(deliveryMeeting.id.eq(meeting.id)))
						.otherwise(JPAExpressions
							.select(offlineMeeting.meetingPlace)
							.from(offlineMeeting)
							.where(offlineMeeting.id.eq(meeting.id))),
					"location"),
				ExpressionUtils.as(
					new CaseBuilder()
						.when(meeting.type.eq("DELIVERY"))
						.then(JPAExpressions
							.select(deliveryMeeting.orderDeadline)
							.from(deliveryMeeting)
							.where(deliveryMeeting.id.eq(meeting.id)))
						.otherwise(JPAExpressions
							.select(offlineMeeting.meetingDate)
							.from(offlineMeeting)
							.where(offlineMeeting.id.eq(meeting.id))),
					"dueDateTime"),
				ExpressionUtils.as(
					JPAExpressions
						.select(meetingParticipant.count())
						.from(meetingParticipant)
						.where(meetingParticipant.meeting.id.eq(meeting.id)),
					"participantCount")
			))
			.from(meeting)
			.join(meetingParticipant).on(
				meetingParticipant.meeting.id.eq(meeting.id),
				meetingParticipant.member.memberId.eq(memberId)
			)
			.where(statusCondition, roleCondition, cursorCondition)
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
			.limit(pageSize + 1)
			.fetch();
	}

	private BooleanExpression getCursorCondition(Long lastMeetingId, LocalDateTime lastDateTime) {
		if (lastMeetingId == null || lastDateTime == null) {
			return null;
		}

		return new CaseBuilder()
			.when(meeting.meetingStatus.ne(MeetingStatus.INACTIVE))
			.then(1)
			.otherwise(0)
			.eq(new CaseBuilder()
				.when(meeting.meetingStatus.ne(MeetingStatus.INACTIVE))
				.then(1)
				.otherwise(0))
			.and(new CaseBuilder()
				.when(meeting.type.eq("DELIVERY"))
				.then(deliveryMeeting.orderDeadline)
				.otherwise(offlineMeeting.meetingDate)
				.gt(lastDateTime)
				.or(new CaseBuilder()
					.when(meeting.type.eq("DELIVERY"))
					.then(deliveryMeeting.orderDeadline)
					.otherwise(offlineMeeting.meetingDate)
					.eq(lastDateTime)
					.and(meeting.id.lt(lastMeetingId))
				)
			);
	}

	@Override
	public UpcomingMeetingResponseDto findUpcomingMeeting(Long memberId) {
		BooleanExpression isDelivery = meeting.type.eq("DELIVERY");

		return queryFactory
			.select(Projections.constructor(UpcomingMeetingResponseDto.class,
				meetingParticipant.member.nickname,
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
					"meetingLocation")
			))
			.from(meeting)
			.join(meetingParticipant).on(
				meetingParticipant.meeting.id.eq(meeting.id),
				meetingParticipant.member.memberId.eq(memberId)
			)
			.where(meeting.meetingStatus.eq(MeetingStatus.ACTIVE))
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
			.fetchFirst();
	}

	@Override
	public List<MeetingListResponseDto> findDeliveryMeetingList(FoodCategory category,
		GenderRestriction genderRestriction, Long maxParticipant, Long minParticipant, MeetingSortType sortType,
		int pageSize, Long lastMeetingId, LocalDateTime lastDateTime) {

		// 카테고리 제한 조건
		BooleanExpression isCategory = category != null ?
			deliveryMeeting.foodCategory.eq(category) : null;

		// 모임 시간 관련 expression
		DateTimeExpression<LocalDateTime> meetingTimeExpr = deliveryMeeting.orderDeadline;

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
			createDeliveryMeetingProjection(),
			deliveryMeeting,
			deliveryMeeting.id
		);
	}

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

		// 카테고리 제한 조건
		BooleanExpression isCategory = category != null ?
			offlineMeeting.offlineMeetingCategory.eq(category) : null;

		// 모임 시간 관련 expression
		DateTimeExpression<LocalDateTime> meetingTimeExpr = offlineMeeting.meetingDate;

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
			createOfflineMeetingProjection(),
			offlineMeeting,
			offlineMeeting.id
		);
	}

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
			.select(projection)
			.from(meeting)
			.join(joinTable).on(joinTableId.eq(meeting.id))
			.where(
				meeting.meetingStatus.eq(MeetingStatus.ACTIVE),
				categoryCondition,
				genderCondition,
				participantCondition,
				cursorCondition
			)
			.orderBy(createOrderSpecifier(sortType, participantCount, meetingTimeExpr))
			.limit(pageSize + 1)
			.fetch();
	}

	private ConstructorExpression<MeetingListResponseDto> createDeliveryMeetingProjection() {
		return Projections.constructor(MeetingListResponseDto.class,
			meeting.id,
			meeting.meetingName,
			meeting.meetingDescription,
			calculateParticipantCount(),
			meeting.participantLimit.maxParticipants,
			deliveryMeeting.storeName.as("location"),
			meeting.createdAt,
			deliveryMeeting.orderDeadline.as("dueDateTime")
		);
	}

	private ConstructorExpression<MeetingListResponseDto> createOfflineMeetingProjection() {
		return Projections.constructor(MeetingListResponseDto.class,
			meeting.id,
			meeting.meetingName,
			meeting.meetingDescription,
			calculateParticipantCount(),
			meeting.participantLimit.maxParticipants,
			offlineMeeting.meetingPlace.as("location"),
			meeting.createdAt,
			offlineMeeting.meetingDate.as("dueDateTime")
		);
	}

	private NumberExpression<Long> calculateParticipantCount() {
		return Expressions.asNumber(
			JPAExpressions
				.select(meetingParticipant.count())
				.from(meetingParticipant)
				.where(meetingParticipant.meeting.eq(meeting))
		).castToNum(Long.class);
	}

	private BooleanExpression createGenderCondition(GenderRestriction genderRestriction) {
		return genderRestriction != null ?
			meeting.genderRestriction.eq(genderRestriction) : null;
	}

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

	private BooleanExpression createCursorCondition(Long lastMeetingId, LocalDateTime lastDateTime,
		MeetingSortType sortType, DateTimeExpression<LocalDateTime> meetingTimeExpr) {
		if (lastMeetingId == null || lastDateTime == null) {
			return null;
		}

		// 정렬 타입에 따른 커서 조건 생성
		switch (sortType) {
			case CREATED_AT:
				return meeting.createdAt.lt(lastDateTime)
					.or(meeting.createdAt.eq(lastDateTime)
						.and(meeting.id.lt(lastMeetingId)));

			case MEETING_TIME:
				return meetingTimeExpr.gt(lastDateTime)
					.or(meetingTimeExpr.eq(lastDateTime)
						.and(meeting.id.gt(lastMeetingId)));

			case PARTICIPANT_COUNT:
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

	private OrderSpecifier<?> createOrderSpecifier(
		MeetingSortType sortType,
		NumberExpression<Long> participantCount,
		Expression<LocalDateTime> meetingTimeExpr) {

		if (sortType == null) {
			return participantCount.desc();
		}

		switch (sortType) {
			case CREATED_AT:
				return meeting.createdAt.desc();
			case MEETING_TIME:
				return new OrderSpecifier<>(Order.ASC, meetingTimeExpr);
			case PARTICIPANT_COUNT:
				return participantCount.desc();
			default:
				return participantCount.desc();
		}
	}
}
