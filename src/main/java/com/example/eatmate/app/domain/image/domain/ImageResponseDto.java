package com.example.eatmate.app.domain.image.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ImageResponseDto {

	private Long id;

	private String imageUrl;

	private ImageType type;    // PROFILE, MEETING_BACKGROUND

	@Builder
	private ImageResponseDto(Long id, String imageUrl, ImageType type) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.type = type;
	}

	public static ImageResponseDto from(Image image) {
		return ImageResponseDto.builder()
			.id(image.getId())
			.imageUrl(image.getImageUrl())
			.type(image.getType())
			.build();
	}
}
