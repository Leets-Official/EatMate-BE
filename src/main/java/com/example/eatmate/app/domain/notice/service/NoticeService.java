package com.example.eatmate.app.domain.notice.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.notice.domain.Notice;
import com.example.eatmate.app.domain.notice.domain.repository.NoticeRepository;
import com.example.eatmate.app.domain.notice.dto.NoticeAdminRequestDto;
import com.example.eatmate.app.domain.notice.dto.NoticeIdResponseDto;
import com.example.eatmate.app.domain.notice.dto.NoticeResponseDto;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final NoticeRepository noticeRepository;

	public NoticeIdResponseDto createNotice(NoticeAdminRequestDto noticeAdminRequestDto) {

		Notice notice = Notice.createNotice(noticeAdminRequestDto.getTitle(), noticeAdminRequestDto.getContent());
		noticeRepository.save(notice);

		return NoticeIdResponseDto.from(notice.getId());
	}

	public NoticeResponseDto findNotice(Long noticeId) {

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOTICE_NOT_FOUND));

		return NoticeResponseDto.from(notice);
	}

	public Slice<NoticeResponseDto> findNotices(int pageNumber, int pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
		Slice<Notice> notices = noticeRepository.findPageBy(pageable);

		return notices.map(NoticeResponseDto::from);
	}

	public NoticeIdResponseDto updateNotice(Long noticeId, NoticeAdminRequestDto noticeAdminRequestDto) {

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOTICE_NOT_FOUND));

		notice.update(noticeAdminRequestDto.getTitle(), noticeAdminRequestDto.getContent());

		noticeRepository.save(notice);

		return NoticeIdResponseDto.from(noticeId);
	}

	public NoticeIdResponseDto deleteNotice(Long noticeId) {

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOTICE_NOT_FOUND));

		noticeRepository.delete(notice);

		return NoticeIdResponseDto.from(noticeId);
	}
}
