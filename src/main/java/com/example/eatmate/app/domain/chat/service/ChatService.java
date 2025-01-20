package com.example.eatmate.app.domain.chat.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.chat.domain.Chat;
import com.example.eatmate.app.domain.chat.domain.repository.ChatRepository;
import com.example.eatmate.app.domain.chat.dto.request.ChatMessageRequestDto;
import com.example.eatmate.app.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.repository.ChatRoomRepository;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.common.DeletedStatus;
import com.example.eatmate.global.common.util.SecurityUtils;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	//채팅 저장 -> 추후 몽고디비 고려
	public void saveChat(ChatMessageRequestDto chatMessageDto, UserDetails userDetails) {
		ChatRoom chatRoom = chatRoomRepository.findById(chatMessageDto.getChatRoomId())
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));
		Member member = securityUtils.getMember(userDetails);
		Chat chat = Chat.createChat(chatMessageDto.getContent(), member, chatRoom);
		chatRepository.save(chat);
	}

	//불러오기(읽기 상태 제외)
	public Page<ChatMessageResponseDto> loadChat(Long chatRoomId, Pageable pageable) {
		ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedStatusNot(chatRoomId, DeletedStatus.NOT_DELETED)
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));

		Page<Chat> chats = chatRepository.findChatByChatRoom(chatRoom, pageable);

		return chats.map(ChatMessageResponseDto::from);
	}

	//채팅 삭제
	public void deleteChat(ChatRoom chatRoom) {
		List<Chat> chatList = chatRepository.findChatByChatRoomAndDeletedStatusNot(chatRoom, DeletedStatus.NOT_DELETED);
		chatList.forEach(Chat::deleteChat);
	}
}
