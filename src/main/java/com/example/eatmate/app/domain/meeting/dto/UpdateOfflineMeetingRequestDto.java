package com.example.eatmate.app.domain.meeting.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.example.eatmate.app.domain.meeting.domain.OfflineMeetingCategory;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateOfflineMeetingRequestDto {
	@Size(max = 30, message = "모임 이름은 30자 이하여야 합니다")
	private String meetingName;

	@Size(max = 100, message = "설명은 100자 이하여야 합니다")
	private String meetingDescription;

	private String meetingPlace;

	@Future(message = "모임 시간은 현재 시간 이후여야 합니다")
	private LocalDateTime meetingDate;

	private OfflineMeetingCategory offlineMeetingCategory;

	private MultipartFile backgroundImage;
}
