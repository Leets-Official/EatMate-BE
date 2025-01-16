package com.example.eatmate.app.domain.chatRoom.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.chatRoom.domain.MemberChatRoom;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {

	Optional<MemberChatRoom> findByMember_MemberId(Long memberId);
}