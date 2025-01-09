package com.example.eatmate.app.domain.chatRoom.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.global.common.DeletedStatus;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	Optional<ChatRoom> findByIdAndDeletedStatusNot(long id, DeletedStatus deletedStatus);

	Optional<ChatRoom> findByMeetingIdAndDeletedStatusNot(long meetingId, DeletedStatus deletedStatus);
}
