package com.example.eatmate.app.domain.chatRoom.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.eatmate.app.domain.chat.service.ChatService;
import com.example.eatmate.app.domain.chat.service.QueueManager;
import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.DeletedStatus;
import com.example.eatmate.app.domain.chatRoom.domain.MemberChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.repository.ChatRoomRepository;
import com.example.eatmate.app.domain.chatRoom.domain.repository.MemberChatRoomRepository;
import com.example.eatmate.app.domain.chatRoom.dto.response.ChatRoomDeliveryNoticeDto;
import com.example.eatmate.app.domain.chatRoom.dto.response.ChatRoomOfflineNoticeDto;
import com.example.eatmate.app.domain.chatRoom.dto.response.ChatRoomResponseDto;
import com.example.eatmate.app.domain.chatRoom.event.HostChatRoomLeftEvent;
import com.example.eatmate.app.domain.chatRoom.event.ParticipantChatRoomLeftEvent;
import com.example.eatmate.app.domain.meeting.domain.DeliveryMeeting;
import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.app.domain.meeting.domain.OfflineMeeting;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.global.common.util.SecurityUtils;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final MemberChatRoomRepository memberChatRoomRepository;
	private final ChatService chatService;
	private final QueueManager queueManager;
	private final SecurityUtils securityUtils;
	private final ApplicationEventPublisher eventPublisher;

	//채팅방 생성 + 호스트 채팅방 참가
	public ChatRoom createChatRoom(Member host, Meeting meeting) {
		ChatRoom newChatRoom = ChatRoom.createChatRoom(host.getMemberId(), meeting);
		chatRoomRepository.save(newChatRoom);

		newChatRoom.addParticipant(memberChatRoomRepository.save(MemberChatRoom.create(newChatRoom, host)));
		queueManager.createQueueForChatRoom(newChatRoom.getId());

		return newChatRoom;
	}

	//모임 참여 -> 유저채팅방 생성 + 참가자 추가
	public void joinChatRoom(Long meetingId, UserDetails userDetails) {
		ChatRoom chatRoom = chatRoomRepository.findByMeetingIdAndDeletedStatus(meetingId, DeletedStatus.NOT_DELETED)
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));

		Member participant = securityUtils.getMember(userDetails);

		chatRoom.addParticipant(memberChatRoomRepository.save(MemberChatRoom.create(chatRoom, participant)));
	}

	//채팅방 입장(지난 로딩 위치는 클라이언트에서 조절)
	public ChatRoomResponseDto enterChatRoomAndLoadMessage(Long chatRoomId, UserDetails userDetails, Pageable pageable) {
		securityUtils.getMember(userDetails);
		ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedStatus(chatRoomId, DeletedStatus.NOT_DELETED)
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));

		List<ChatRoomResponseDto.ChatMemberResponseDto> participants = chatRoom.getParticipant()
			.stream()
			.map(memberChatRoom -> ChatRoomResponseDto.ChatMemberResponseDto.from(memberChatRoom.getMember()))
			.collect(Collectors.toList());

		Page<ChatMessageResponseDto> chatList = chatService.loadChat(chatRoomId, pageable);

		//채팅방 공지 처리
		Meeting meeting = chatRoom.getMeeting();
		if (meeting instanceof OfflineMeeting) {
			OfflineMeeting offlineMeeting = (OfflineMeeting) meeting;
			ChatRoomOfflineNoticeDto notice = ChatRoomOfflineNoticeDto.of(offlineMeeting.getMeetingPlace(), offlineMeeting.getMeetingDate());

			return ChatRoomResponseDto.ofWithOffline(participants, chatList, notice);
		}

		if (meeting instanceof DeliveryMeeting) {
			DeliveryMeeting deliveryMeeting = (DeliveryMeeting) meeting;
			ChatRoomDeliveryNoticeDto notice = ChatRoomDeliveryNoticeDto
				.of(deliveryMeeting.getStoreName(), deliveryMeeting.getAccountNumber(), deliveryMeeting.getBankName().toString(), deliveryMeeting.getPickupLocation());

			return ChatRoomResponseDto.ofWithDelivery(participants, chatList, notice);
		}

		throw new CommonException(ErrorCode.INVALID_MEETING_TYPE);
	}

	//나가기(두 가지) + 큐 해제
	public Void leaveChatRoom(Long chatRoomId, UserDetails userDetails) {
		Member member = securityUtils.getMember(userDetails);
		ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedStatus(chatRoomId, DeletedStatus.NOT_DELETED)
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));

		MemberChatRoom target = memberChatRoomRepository.findByMember_MemberId(member.getMemberId())
			.orElseThrow(() -> new CommonException(ErrorCode.MEMBER_CHATROOM_NOT_FOUND));

		if(chatRoom.getOwnerId().equals(member.getMemberId())) {
			chatRoom.deleteChatRoom();
			chatRoom.getParticipant().forEach(memberChatRoomRepository::delete);
			chatService.deleteChat(chatRoom);
			queueManager.deleteQueueForChatRoom(chatRoomId);
			queueManager.stopChatRoomListener(chatRoomId);
			eventPublisher.publishEvent(new HostChatRoomLeftEvent(chatRoom.getMeeting().getId(), userDetails));
		}
		else {
			chatRoom.removeParticipant(target);
			memberChatRoomRepository.delete(target);
			eventPublisher.publishEvent(new ParticipantChatRoomLeftEvent(chatRoom.getMeeting().getId(), userDetails));
		}
		return null;
	}
}
