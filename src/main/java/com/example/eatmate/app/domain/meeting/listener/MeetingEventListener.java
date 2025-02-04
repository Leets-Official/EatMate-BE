package com.example.eatmate.app.domain.meeting.listener;

import static org.springframework.transaction.event.TransactionPhase.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.event.NoticeMemberLeftEvent;
import com.example.eatmate.app.domain.chatRoom.service.ChatRoomService;
import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.app.domain.meeting.domain.repository.MeetingRepository;
import com.example.eatmate.app.domain.meeting.event.HostMeetingDeleteEvent;
import com.example.eatmate.app.domain.meeting.event.MeetingCreatedEvent;
import com.example.eatmate.app.domain.meeting.event.MeetingJoinedEvent;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MeetingEventListener {
	private final ChatRoomService chatRoomService;
	private final MeetingRepository meetingRepository;

	@TransactionalEventListener(phase = BEFORE_COMMIT)
	public void handleMeetingCreatedEvent(MeetingCreatedEvent event) {
		Meeting meeting = meetingRepository.findById(event.getMeetingId())
			.orElseThrow(() -> new CommonException(ErrorCode.MEETING_NOT_FOUND));
		ChatRoom chatroom = chatRoomService.createChatRoom(event.getHost(), meeting);
		meeting.setChatRoom(chatroom);
	}

	@TransactionalEventListener(phase = BEFORE_COMMIT)
	public void handleMeetingJoinedEvent(MeetingJoinedEvent event) {
		chatRoomService.joinChatRoom(event.getMeetingId(), event.getParticipant());
	}

	@TransactionalEventListener(phase = BEFORE_COMMIT)
	public void handleHostMeetingDeletedEvent(HostMeetingDeleteEvent event) {
		chatRoomService.leaveChatRoom(event.getChatRoomId(), event.getUserDetails());
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMemberLeftMeetingAndNotice(NoticeMemberLeftEvent event) {
		chatRoomService.sendLeveMessage(event.getChatRoom(), event.getMember());
	}

}
