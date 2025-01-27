package com.example.eatmate.app.domain.chatRoom.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.eatmate.app.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.eatmate.app.domain.member.domain.Mbti;
import com.example.eatmate.app.domain.member.domain.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomResponseDto {
	private List<ChatMessageResponseDto> chats;
	private List<ChatMemberResponseDto> participants;
	private ChatRoomDeliveryNoticeDto deliveryNotice;
	private ChatRoomOfflineNoticeDto offlineNotice;
	private int pageNumber;
	private boolean isLast;

	@Builder
	private ChatRoomResponseDto(Page<ChatMessageResponseDto> chats, List<ChatMemberResponseDto> participants,
		ChatRoomDeliveryNoticeDto deliveryNotice, ChatRoomOfflineNoticeDto offlineNotice) {
		this.chats = chats.getContent();
		this.participants = participants;
		this.deliveryNotice = deliveryNotice;
		this.offlineNotice = offlineNotice;
		this.pageNumber = chats.getNumber() + 1;
		this.isLast = chats.isLast();
	}

	public static ChatRoomResponseDto ofWithDelivery(List<ChatMemberResponseDto> participants, Page<ChatMessageResponseDto> chatPage, ChatRoomDeliveryNoticeDto deliveryNotice) {
		return ChatRoomResponseDto.builder()
			.chats(chatPage)
			.participants(participants)
			.deliveryNotice(deliveryNotice)
			.build();
	}

	public static ChatRoomResponseDto ofWithOffline(List<ChatMemberResponseDto> participants, Page<ChatMessageResponseDto> chatPage, ChatRoomOfflineNoticeDto offlineNotice) {
		return ChatRoomResponseDto.builder()
			.chats(chatPage)
			.participants(participants)
			.offlineNotice(offlineNotice)
			.build();
	}

	@Getter
	public static class ChatMemberResponseDto {
		private String memberName;
		private Mbti mbti;
		private String profileUrl;

		@Builder
		private ChatMemberResponseDto(String memberName, Mbti mbti, String profileUrl) {
			this.memberName = memberName;
			this.mbti = mbti;
			this.profileUrl = profileUrl;
		}

		public static ChatMemberResponseDto from(Member member) {
			return ChatMemberResponseDto.builder()
				.memberName(member.getName())
				.mbti(member.getMbti())
				.profileUrl(member.getProfileImage().toString())
				.build();
		}
	}
}
