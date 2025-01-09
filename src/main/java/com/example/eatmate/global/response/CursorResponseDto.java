package com.example.eatmate.global.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.eatmate.app.domain.meeting.dto.MeetingListResponseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CursorResponseDto<T> {
	private List<T> content;
	private boolean hasNext;
	private Long lastId;
	private LocalDateTime lastDateTime;

	public CursorResponseDto(List<T> content, boolean hasNext, Long lastId, LocalDateTime lastDateTime) {
		this.content = content;
		this.hasNext = hasNext;
		this.lastId = lastId;
		this.lastDateTime = lastDateTime;
	}

	public static <T> CursorResponseDto<T> of(List<T> content, int pageSize) {
		boolean hasNext = content.size() > pageSize;
		// 실제 요청한 크기보다 1개 더 조회했으므로, 마지막 데이터는 제거
		List<T> result = hasNext ? content.subList(0, content.size() - 1) : content;

		if (result.isEmpty()) {
			return new CursorResponseDto<>(result, false, null, null);
		}

		MeetingListResponseDto lastItem = (MeetingListResponseDto)result.get(result.size() - 1);
		LocalDateTime lastDateTime =
			"DELIVERY".equals(lastItem.getMeetingType()) ? lastItem.getOrderDeadline() : lastItem.getMeetingDate();
		return new CursorResponseDto<>(
			result,
			hasNext,
			lastItem.getId(),
			lastDateTime // orderDeadline 또는 meetingDate
		);
	}
}
