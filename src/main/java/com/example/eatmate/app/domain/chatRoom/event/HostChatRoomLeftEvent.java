package com.example.eatmate.app.domain.chatRoom.event;

import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HostChatRoomLeftEvent {
	private final Long meetingId;
	private final UserDetails userDetails;
}
