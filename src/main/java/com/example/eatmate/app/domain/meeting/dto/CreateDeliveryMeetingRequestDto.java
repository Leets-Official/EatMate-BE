package com.example.eatmate.app.domain.meeting.dto;

import org.springframework.web.multipart.MultipartFile;

import com.example.eatmate.app.domain.meeting.domain.BankName;
import com.example.eatmate.app.domain.meeting.domain.FoodCategory;
import com.example.eatmate.app.domain.meeting.domain.GenderRestriction;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateDeliveryMeetingRequestDto {
	@NotBlank(message = "모임 이름은 필수입니다")
	@Size(max = 30, message = "모임 이름은 30자 이하여야 합니다")
	private String meetingName;

	@Size(max = 100, message = "설명은 100자 이하여야 합니다")
	private String meetingDescription;

	@NotNull(message = "성별 제한 설정은 필수입니다")
	private GenderRestriction genderRestriction;

	@NotNull(message = "인원 제한 여부는 필수입니다")
	private Boolean isLimited;

	@Min(value = 2, message = "참여 인원은 최소 2명 이상이어야 합니다")
	@Max(value = 10, message = "참여 인원은 최대 10명까지 가능합니다")
	private Long maxParticipants;

	@NotNull(message = "음식 카테고리는 필수입니다")
	private FoodCategory foodCategory;

	@NotBlank(message = "가게 이름은 필수입니다")
	private String storeName;

	@NotBlank(message = "픽업 위치는 필수입니다")
	private String pickupLocation;

	@NotNull(message = "마감 시간은 필수입니다")
	@Positive(message = "마감 시간은 양수여야 합니다")
	@Max(value = 180, message = "모임은 최대 3시간까지만 가능합니다")
	private Long orderDeadline;

	@NotBlank(message = "계좌번호는 필수입니다")
	@Pattern(regexp = "^[0-9-]*$", message = "올바른 계좌번호 형식이 아닙니다")
	private String accountNumber;

	@NotBlank(message = "은행명은 필수입니다")
	private BankName bankName;

	private MultipartFile backgroundImage;
}
