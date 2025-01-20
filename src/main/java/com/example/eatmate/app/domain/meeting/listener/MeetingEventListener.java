package com.example.eatmate.app.domain.meeting.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.eatmate.app.domain.chatRoom.service.ChatRoomService;
import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingRepository;
import com.example.eatmate.app.domain.meeting.event.MeetingCreatedEvent;
import com.example.eatmate.app.domain.meeting.event.MeetingJoinedEvent;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetingEventListener {
	private final ChatRoomService chatRoomService;
	private final MeetingRepository meetingRepository;

	@EventListener
	@Transactional
	public void handleMeetingCreatedEvent(MeetingCreatedEvent event) {
		Meeting meeting = meetingRepository.findById(event.getMeetingId())
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));
		chatRoomService.createChatRoom(event.getHost(), meeting);
	}

	@EventListener
	@Transactional
	public void handleMeetingJoinedEvent(MeetingJoinedEvent event) {
		chatRoomService.joinChatRoom(event.getMeetingId(), event.getParticipant());
	}

}
