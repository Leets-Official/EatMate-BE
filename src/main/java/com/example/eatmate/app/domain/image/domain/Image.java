package com.example.eatmate.app.domain.image.domain;

import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Image extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "image_url")
	private String imageUrl;

	@Enumerated(EnumType.STRING)
	private ImageType type;    // PROFILE, MEETING_BACKGROUND

	@Builder
	private Image(String imageUrl, ImageType type) {
		this.imageUrl = imageUrl;
		this.type = type;
	}

	public static Image createImage(String imageUrl, ImageType type) {
		return Image.builder()
			.imageUrl(imageUrl)
			.type(type)
			.build();
	}

}
