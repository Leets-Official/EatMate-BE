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

	// 기본 버전 (id와 시간만 필요한 경우)
	public static <T> CursorResponseDto<T> of(
		List<T> content,
		Long pageSize,
		Function<T, Long> idExtractor,
		Function<T, LocalDateTime> dateTimeExtractor) {

		boolean hasNext = content.size() > pageSize;
		List<T> result = hasNext ? content.subList(0, content.size() - 1) : content;

		if (result.isEmpty()) {
			return new CursorResponseDto<>(result, false, null);
		}

		T lastItem = result.get(result.size() - 1);
		CursorInfo cursorInfo = new CursorInfo(
			idExtractor.apply(lastItem),
			dateTimeExtractor.apply(lastItem)
		);

		return new CursorResponseDto<>(result, hasNext, cursorInfo);
	}

	// 확장 버전 (모든 정렬 기준이 필요한 경우)
	public static <T> CursorResponseDto<T> of(
		List<T> content,
		Long pageSize,
		Function<T, Long> idExtractor,
		Function<T, LocalDateTime> createdAtExtractor,
		Function<T, LocalDateTime> meetingTimeExtractor) {

		boolean hasNext = content.size() > pageSize;
		List<T> result = hasNext ? content.subList(0, content.size() - 1) : content;

		if (result.isEmpty()) {
			return new CursorResponseDto<>(result, false, null);
		}

		T lastItem = result.get(result.size() - 1);
		CursorInfo cursorInfo = new CursorInfo(
			idExtractor.apply(lastItem),
			createdAtExtractor.apply(lastItem),
			meetingTimeExtractor.apply(lastItem)
		);

		return new CursorResponseDto<>(result, hasNext, cursorInfo);
	}
}
