package com.example.eatmate.app.domain.chatRoom.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.DeletedStatus;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	/*@Query("SELECT c FROM ChatRoom c WHERE c.meeting.id = :id AND c.deletedStatus = com.example.eatmate.app.domain.chatRoom.domain.DeletedStatus")
	Optional<ChatRoom> findByMeetingId(@Param("id") Long id);*/
	@Query("SELECT c FROM ChatRoom c WHERE c.meeting.id = :id AND c.deletedStatus = :deletedStatus")
	Optional<ChatRoom> findByMeetingId(@Param("id") Long id, @Param("deletedStatus") DeletedStatus deletedStatus);
}