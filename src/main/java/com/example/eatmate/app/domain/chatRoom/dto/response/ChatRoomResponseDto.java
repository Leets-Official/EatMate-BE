package com.example.eatmate.app.domain.chatRoom.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.example.eatmate.app.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.eatmate.app.domain.meeting.domain.MeetingParticipant;
import com.example.eatmate.app.domain.meeting.domain.ParticipantRole;
import com.example.eatmate.app.domain.member.domain.Mbti;

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
		private Long memberId;
		private String nickname;
		private Mbti mbti;
		private String profileImageUrl;
		private ParticipantRole role;

		@Builder
		private ChatMemberResponseDto(Long memberId, String nickname, Mbti mbti, String profileImageUrl, ParticipantRole role) {
			this.memberId = memberId;
			this.nickname = nickname;
			this.mbti = mbti;
			this.profileImageUrl = profileImageUrl;
			this.role = role;
		}

		public static ChatMemberResponseDto from(MeetingParticipant participant) {
			return ChatMemberResponseDto.builder()
				.memberId(participant.getMember().getMemberId())
				.nickname(participant.getMember().getNickname())
				.mbti(participant.getMember().getMbti())
				.profileImageUrl(participant.getMember().getProfileImage().getImageUrl())
				.role(participant.getRole())
				.build();
		}
	}
}
