package com.example.eatmate.app.domain.chatRoom.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.MemberChatRoom;
import com.example.eatmate.app.domain.member.domain.Member;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

	Optional<MemberChatRoom> findByMember_MemberId(Long memberId);

	Optional<MemberChatRoom> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);
}
