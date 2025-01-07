package com.example.eatmate.app.domain.notice.service;

import org.springframework.stereotype.Service;

import com.example.eatmate.app.domain.notice.domain.Notice;
import com.example.eatmate.app.domain.notice.domain.repository.NoticeRepository;
import com.example.eatmate.app.domain.notice.dto.NoticeAdminRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

	private final NoticeRepository noticeRepository;

	public void createNotice(NoticeAdminRequestDto noticeAdminRequestDto) {
		Notice notice = Notice.createNotice(noticeAdminRequestDto);
		noticeRepository.save(notice);
	}
}
