package com.example.eatmate.app.domain.notice.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eatmate.app.domain.notice.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	Slice<Notice> findPageBy(Pageable pageable);
}
