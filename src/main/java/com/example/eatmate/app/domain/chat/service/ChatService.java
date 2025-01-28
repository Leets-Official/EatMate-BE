package com.example.eatmate.app.domain.chat.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.chat.domain.Chat;
import com.example.eatmate.app.domain.chat.domain.repository.ChatRepository;
import com.example.eatmate.app.domain.chat.dto.request.ChatMessageRequestDto;
import com.example.eatmate.app.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.DeletedStatus;
import com.example.eatmate.app.domain.chatRoom.domain.repository.ChatRoomRepository;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.global.common.util.SecurityUtils;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

	private final ChatPublisher chatPublisher;
	private final ChatRepository chatRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final SecurityUtils securityUtils;

	public void sendChatMessage(ChatMessageRequestDto chatMessageDto, UserDetails userDetail) {
		chatPublisher.sendMessage(chatMessageDto.getChatRoomId(), chatMessageDto);
		saveChat(chatMessageDto, userDetail);
	}

	//채팅 저장
	public void saveChat(ChatMessageRequestDto chatMessageDto, UserDetails userDetails) {
		ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedStatus(chatMessageDto.getChatRoomId(), DeletedStatus.NOT_DELETED)
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));
		Member member = securityUtils.getMember(userDetails);

		Chat chat = Chat.createChat(chatMessageDto.getContent(), member, chatRoom);
		chatRepository.save(chat);

		chatRoom.updateLastChat(chat.getContent());
		chatRoom.updateLastChatAt(chat.getCreatedAt());
	}

	//불러오기
	public Slice<ChatMessageResponseDto> loadChat(Long chatRoomId, LocalDateTime cursor, Pageable pageable) {
		ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedStatus(chatRoomId, DeletedStatus.NOT_DELETED)
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));

		try{
			if(cursor == null) {
				Slice<Chat> chats = chatRepository.findChatByChatRoomOrderByCreatedAtDesc(chatRoom, pageable);

				return chats.map(ChatMessageResponseDto::from);
			}
			else {
				Slice<Chat> chats = chatRepository.findChatByChatRoomAndCreatedAtLessThanOrderByCreatedAtDesc(chatRoom, cursor, pageable);

				return chats.map(ChatMessageResponseDto::from);
			}
		} catch (Exception e) {
			throw new CommonException(ErrorCode.CHAT_NOT_FOUND);
		}
	}

	//채팅 삭제
	public void deleteChat(ChatRoom chatRoom) {
		List<Chat> chatList = chatRepository.findChatByChatRoomAndDeletedStatusNot(chatRoom, DeletedStatus.NOT_DELETED);
		chatList.forEach(Chat::deleteChat);
	}
}
