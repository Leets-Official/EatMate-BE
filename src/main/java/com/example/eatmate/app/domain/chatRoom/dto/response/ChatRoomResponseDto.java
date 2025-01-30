package com.example.eatmate.app.domain.chatRoom.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

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
	private ChatRoomResponseDto(Slice<ChatMessageResponseDto> chats, List<ChatMemberResponseDto> participants,
		ChatRoomDeliveryNoticeDto deliveryNotice, ChatRoomOfflineNoticeDto offlineNotice) {
		this.chats = chats.getContent();
		this.participants = participants;
		this.deliveryNotice = deliveryNotice;
		this.offlineNotice = offlineNotice;
		this.pageNumber = chats.getNumber() + 1;
		this.isLast = chats.isLast();
	}

	public static ChatRoomResponseDto ofWithDelivery(List<ChatMemberResponseDto> participants, Slice<ChatMessageResponseDto> chatPage, ChatRoomDeliveryNoticeDto deliveryNotice) {
		return ChatRoomResponseDto.builder()
			.chats(chatPage)
			.participants(participants)
			.offlineNotice(null)
			.deliveryNotice(deliveryNotice)
			.build();
	}

	public static ChatRoomResponseDto ofWithOffline(List<ChatMemberResponseDto> participants, Slice<ChatMessageResponseDto> chatPage, ChatRoomOfflineNoticeDto offlineNotice) {
		return ChatRoomResponseDto.builder()
			.chats(chatPage)
			.participants(participants)
			.offlineNotice(offlineNotice)
			.deliveryNotice(null)
			.build();
	}

	@Getter
	public static class ChatMemberResponseDto {
		private String nickname;
		private Mbti mbti;
		private String profileImageUrl;

		@Builder
		private ChatMemberResponseDto(String nickname, Mbti mbti, String profileImageUrl) {
			this.nickname = nickname;
			this.mbti = mbti;
			this.profileImageUrl = profileImageUrl;
		}

		public static ChatMemberResponseDto from(Member member) {
			return ChatMemberResponseDto.builder()
				.nickname(member.getNickname())
				.mbti(member.getMbti())
				.profileImageUrl(member.getProfileImage().getImageUrl())
				.build();
		}
	}
}
