package com.example.eatmate.app.domain.chat.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.chat.domain.Chat;
import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.DeletedStatus;

public interface ChatRepository extends JpaRepository<Chat, Long> {

	Slice<Chat> findChatByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom, Pageable pageable);

	Slice<Chat> findChatByChatRoomAndCreatedAtLessThanOrderByCreatedAtDesc(ChatRoom chatRoom, LocalDateTime createdAt, Pageable pageable);

	List<Chat> findChatByChatRoomAndDeletedStatusNot(ChatRoom chatRoom, DeletedStatus deletedStatus);
}
