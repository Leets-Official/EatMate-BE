package com.example.eatmate.app.domain.chat.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.chat.domain.Chat;
import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.DeletedStatus;

public interface ChatRepository extends JpaRepository<Chat, Long> {

	Page<Chat> findChatByChatRoom(ChatRoom chatRoom, Pageable pageable);

	List<Chat> findChatByChatRoomAndDeletedStatusNot(ChatRoom chatRoom, DeletedStatus deletedStatus);
}