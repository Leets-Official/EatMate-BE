package com.example.eatmate.app.domain.image.domain;

import com.example.eatmate.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

	@Builder
	private Image(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public static Image createImage(String imageUrl) {
		return Image.builder()
			.imageUrl(imageUrl)
			.build();
	}

}
