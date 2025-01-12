package com.example.eatmate.app.domain.notice.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.notice.domain.Notice;
import com.example.eatmate.app.domain.notice.domain.repository.NoticeRepository;
import com.example.eatmate.app.domain.notice.dto.NoticeAdminRequestDto;
import com.example.eatmate.app.domain.notice.dto.NoticeResponseDto;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final NoticeRepository noticeRepository;

	public void createNotice(NoticeAdminRequestDto noticeAdminRequestDto) {

		Notice notice = Notice.createNotice(noticeAdminRequestDto);
		noticeRepository.save(notice);
	}

	public NoticeResponseDto findNotice(Long noticeId) {

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOTICE_NOT_FOUND));

		return NoticeResponseDto.createNoticeResponseDto(notice);
	}

	public Slice<NoticeResponseDto> findNotices(int pageNumber, int pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
		Slice<Notice> notices = noticeRepository.findPageBy(pageable);

		return notices.map(NoticeResponseDto::createNoticeResponseDto);
	}

	public void updateNotice(Long noticeId, NoticeAdminRequestDto noticeAdminRequestDto) {

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOTICE_NOT_FOUND));

		notice.update(noticeAdminRequestDto);

		noticeRepository.save(notice);
	}

	public void deleteNotice(Long noticeId) {

		Notice notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new CommonException(ErrorCode.NOTICE_NOT_FOUND));

		noticeRepository.delete(notice);
	}
}
