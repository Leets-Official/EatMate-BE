package com.example.eatmate.app.domain.meeting.dto;

import org.springframework.web.multipart.MultipartFile;

import com.example.eatmate.app.domain.meeting.domain.FoodCategory;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateDeliveryMeetingRequestDto {

	@Size(max = 30, message = "모임 이름은 30자 이하여야 합니다")
	private String meetingName;

	@Size(max = 100, message = "설명은 100자 이하여야 합니다")
	private String meetingDescription;

	private FoodCategory foodCategory;

	@Future
	private String orderDeadline;

	private String storeName;

	private String pickupLocation;

	@Pattern(regexp = "^[0-9-]*$", message = "올바른 계좌번호 형식이 아닙니다")
	private String accountNumber;

	private String accountHolder;

	private MultipartFile backgroundImage;
}
