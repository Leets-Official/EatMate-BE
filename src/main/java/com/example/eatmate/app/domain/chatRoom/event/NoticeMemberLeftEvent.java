package com.example.eatmate.app.domain.chatRoom.event;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.member.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeMemberLeftEvent {
	private ChatRoom chatRoom;
	private Member member;
}

