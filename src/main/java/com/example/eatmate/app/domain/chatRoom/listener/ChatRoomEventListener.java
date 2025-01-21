package com.example.eatmate.app.domain.chatRoom.listener;

import static org.springframework.transaction.event.TransactionPhase.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.eatmate.app.domain.chatRoom.event.HostChatRoomLeftEvent;
import com.example.eatmate.app.domain.chatRoom.event.ParticipantChatRoomLeftEvent;
import com.example.eatmate.app.domain.meeting.service.MeetingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatRoomEventListener {

	private final MeetingService meetingService;

	@TransactionalEventListener(phase = BEFORE_COMMIT)
	public void handleHostChatRoomLeftEvent(HostChatRoomLeftEvent event) {
		meetingService.hostMeetingDelete(event.getMeetingId(), event.getUserDetails(), true);
	}

	@TransactionalEventListener(phase = BEFORE_COMMIT)
	public void handleParticipantChatRoomLeftEvent(ParticipantChatRoomLeftEvent event) {
		meetingService.participantMeetingDelete(event.getMeetingId(), event.getUserDetails());
	}
}
