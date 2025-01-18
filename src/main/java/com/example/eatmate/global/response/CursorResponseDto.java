package com.example.eatmate.global.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CursorResponseDto<T> {
	private List<T> content;
	private boolean hasNext;
	private CursorInfo cursorInfo;

	public CursorResponseDto(List<T> content, boolean hasNext, CursorInfo cursorInfo) {
		this.content = content;
		this.hasNext = hasNext;
		this.cursorInfo = cursorInfo;
	}

	// 공통 처리 로직
	private static <T> List<T> processContent(List<T> content, int pageSize) {
		boolean hasNext = content.size() > pageSize;
		return hasNext ? content.subList(0, content.size() - 1) : content;
	}

	// id만 사용하는 경우
	public static <T> CursorResponseDto<T> ofId(
		List<T> content,
		int pageSize,
		Function<T, Long> idExtractor) {

		List<T> result = processContent(content, pageSize);

		if (result.isEmpty()) {
			return new CursorResponseDto<>(result, false, null);
		}

		T lastItem = result.get(result.size() - 1);
		CursorInfo cursorInfo = CursorInfo.onlyId(idExtractor.apply(lastItem));

		return new CursorResponseDto<>(result, content.size() > pageSize, cursorInfo);
	}

	// id + createdAt 사용하는 경우
	public static <T> CursorResponseDto<T> ofIdAndCreatedAt(
		List<T> content,
		int pageSize,
		Function<T, Long> idExtractor,
		Function<T, LocalDateTime> createdAtExtractor) {

		List<T> result = processContent(content, pageSize);

		if (result.isEmpty()) {
			return new CursorResponseDto<>(result, false, null);
		}

		T lastItem = result.get(result.size() - 1);
		CursorInfo cursorInfo = CursorInfo.withCreatedAt(
			idExtractor.apply(lastItem),
			createdAtExtractor.apply(lastItem)
		);

		return new CursorResponseDto<>(result, content.size() > pageSize, cursorInfo);
	}

	// id + meetingTime 사용하는 경우
	public static <T> CursorResponseDto<T> ofIdAndMeetingTime(
		List<T> content,
		int pageSize,
		Function<T, Long> idExtractor,
		Function<T, LocalDateTime> meetingTimeExtractor) {

		List<T> result = processContent(content, pageSize);

		if (result.isEmpty()) {
			return new CursorResponseDto<>(result, false, null);
		}

		T lastItem = result.get(result.size() - 1);
		CursorInfo cursorInfo = CursorInfo.withMeetingTime(
			idExtractor.apply(lastItem),
			meetingTimeExtractor.apply(lastItem)
		);

		return new CursorResponseDto<>(result, content.size() > pageSize, cursorInfo);
	}
}
