package com.example.eatmate.app.domain.chatRoom.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.DeletedStatus;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	@Query("SELECT c FROM ChatRoom c WHERE c.meeting.id = :id AND c.deletedStatus = :deletedStatus")
	Optional<ChatRoom> findByMeetingIdAndDeletedStatus(@Param("id") Long id, @Param("deletedStatus") DeletedStatus deletedStatus);

	@Query("SELECT c FROM ChatRoom c WHERE c.id = :id AND c.deletedStatus = :deletedStatus")
	Optional<ChatRoom> findByIdAndDeletedStatus(@Param("id") Long id, @Param("deletedStatus") DeletedStatus deletedStatus);

	@Query("SELECT c FROM ChatRoom c WHERE c.deletedStatus = :deletedStatus")
	List<ChatRoom> findAllByDeletedStatus(@Param("deletedStatus") DeletedStatus deletedStatus);

}
