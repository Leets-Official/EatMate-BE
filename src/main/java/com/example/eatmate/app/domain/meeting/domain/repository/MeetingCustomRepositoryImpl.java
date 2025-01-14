package com.example.eatmate.app.domain.meeting.domain.repository;

import static com.example.eatmate.app.domain.meeting.domain.QDeliveryMeeting.*;
import static com.example.eatmate.app.domain.meeting.domain.QMeeting.*;
import static com.example.eatmate.app.domain.meeting.domain.QMeetingParticipant.*;
import static com.example.eatmate.app.domain.meeting.domain.QOfflineMeeting.*;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.meeting.domain.MeetingStatus;
import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.meeting.dto.MyMeetingListResponseDto;
import com.example.eatmate.app.domain.meeting.dto.UpcomingMeetingResponseDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MeetingCustomRepositoryImpl implements MeetingCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<MyMeetingListResponseDto> findAllMeetings(Long memberId, ParticipantRole role,
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
					JPAExpressions
						.select(deliveryMeeting.storeName)
						.from(deliveryMeeting)
						.where(deliveryMeeting.id.eq(meeting.id)),
					"storeName"),
				ExpressionUtils.as(
					JPAExpressions
						.select(offlineMeeting.meetingPlace)
						.from(offlineMeeting)
						.where(offlineMeeting.id.eq(meeting.id)),
					"meetingPlace"),
				ExpressionUtils.as(
					JPAExpressions
						.select(deliveryMeeting.orderDeadline)
						.from(deliveryMeeting)
						.where(deliveryMeeting.id.eq(meeting.id)),
					"orderDeadline"),
				ExpressionUtils.as(
					JPAExpressions
						.select(offlineMeeting.meetingDate)
						.from(offlineMeeting)
						.where(offlineMeeting.id.eq(meeting.id)),
					"meetingDate"),
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
			.limit(pageSize)
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
}
