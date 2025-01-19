package com.example.eatmate.app.domain.meeting.dto;

import org.springframework.web.multipart.MultipartFile;

import com.example.eatmate.app.domain.meeting.domain.FoodCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateDeliveryMeetingRequestDto {

	@NotBlank(message = "모임 이름은 필수입니다")
	@Size(max = 30, message = "모임 이름은 30자 이하여야 합니다")
	private String meetingName;

	@Size(max = 100, message = "설명은 100자 이하여야 합니다")
	private String meetingDescription;

	@NotNull(message = "음식 카테고리는 필수입니다")
	private FoodCategory foodCategory;

	@NotBlank(message = "가게 이름은 필수입니다")
	private String storeName;

	@NotBlank(message = "픽업 위치는 필수입니다")
	private String pickupLocation;

	@NotBlank(message = "계좌번호는 필수입니다")
	@Pattern(regexp = "^[0-9-]*$", message = "올바른 계좌번호 형식이 아닙니다")
	private String accountNumber;

	@NotBlank(message = "예금주명은 필수입니다")
	private String accountHolder;

	private MultipartFile backgroundImage;
}
