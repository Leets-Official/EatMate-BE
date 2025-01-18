package com.example.eatmate.app.domain.chatRoom.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eatmate.app.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.eatmate.app.domain.chat.service.ChatService;
import com.example.eatmate.app.domain.chat.service.QueueManager;
import com.example.eatmate.app.domain.chatRoom.domain.ChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.MemberChatRoom;
import com.example.eatmate.app.domain.chatRoom.domain.repository.ChatRoomRepository;
import com.example.eatmate.app.domain.chatRoom.domain.repository.MemberChatRoomRepository;
import com.example.eatmate.app.domain.chatRoom.dto.response.ChatMemberDto;
import com.example.eatmate.app.domain.chatRoom.dto.response.ChatRoomResponseDto;
import com.example.eatmate.app.domain.meeting.domain.Meeting;
import com.example.eatmate.app.domain.member.domain.Member;
import com.example.eatmate.app.domain.member.domain.repository.MemberRepository;
import com.example.eatmate.global.common.DeletedStatus;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final MemberChatRoomRepository memberChatRoomRepository;
	private final MemberRepository memberRepository;
	private final ChatService chatService;
	private final QueueManager queueManager;

	//채팅방 생성
	public ChatRoom createChatRoom(Member host, Meeting meeting){
		ChatRoom newChatRoom = ChatRoom.createChatRoom(host.getMemberId(), meeting);
		chatRoomRepository.save(newChatRoom);
		memberChatRoomRepository.save(MemberChatRoom.create(newChatRoom,host));
		queueManager.createQueueForChatRoom(newChatRoom.getId());
		return newChatRoom;
	}

	//모임 참여 -> 유저채팅방 생성 + 참가자 추가
	public void joinChatRoom(Long meetingId, UserDetails userDetails) {
		Member participant = getMember(userDetails);
		ChatRoom chatRoom = chatRoomRepository.findByMeetingIdAndDeletedStatusNot(meetingId, DeletedStatus.NOT_DELETED)
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));
		chatRoom.addParticipant(memberChatRoomRepository.save(MemberChatRoom.create(chatRoom, participant)));
	}

	//채팅방 입장(지난 로딩 위치는 FE에서 조절)
	public ChatRoomResponseDto enterChatRoom(Long chatRoomId, UserDetails userDetails, Pageable pageable) {
		Member participant = getMember(userDetails);
		ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedStatusNot(chatRoomId, DeletedStatus.NOT_DELETED)
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));

		List<ChatMemberDto> participants = chatRoom.getParticipant()
			.stream()
			.map(memberChatRoom -> ChatMemberDto.from(memberChatRoom.getMember()))
			.collect(Collectors.toList());

		Page<ChatMessageResponseDto> chatList =  chatService.loadChat(chatRoomId, pageable);
		return ChatRoomResponseDto.of(participants, chatList);
	}

	//나가기(두 가지) + 큐 해제
	//참여 기록 삭제는 항목 비활성화 여부
	public String leaveChatRoom(Long chatRoomId, UserDetails userDetails) {
		Member member = getMember(userDetails);
		ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedStatusNot(chatRoomId, DeletedStatus.NOT_DELETED)
			.orElseThrow(() -> new CommonException(ErrorCode.CHATROOM_NOT_FOUND));

		MemberChatRoom target = memberChatRoomRepository.findByMember_MemberId(member.getMemberId())
			.orElseThrow(() -> new CommonException(ErrorCode.MEMBER_CHATROOM_NOT_FOUND));

		//모임삭제, 참여자 삭제 로직이 추가로 필요합니다.
		if(chatRoom.getOwnerId().equals(member.getMemberId())) {
			chatRoom.deleteChatRoom();
			chatRoom.getParticipant().forEach(memberChatRoomRepository::delete);
			chatService.deleteChat(chatRoom);
			queueManager.deleteQueueForChatRoom(chatRoomId);
			queueManager.stopChatRoomListener(chatRoomId);
		}
		else {
			memberChatRoomRepository.delete(target);
		}
		return "퇴장 완료";
	}

	private Member getMember(UserDetails userDetails) {
		return memberRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));
	}
}
