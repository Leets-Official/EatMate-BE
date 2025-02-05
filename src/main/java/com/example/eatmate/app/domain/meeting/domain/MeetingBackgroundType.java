package com.example.eatmate.app.domain.meeting.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingBackgroundType {
	DEFAULT_IMAGE_1("기본 이미지 1",
		"https://eatmatebucket.s3.ap-northeast-2.amazonaws.com/a2c4c1b796de95f92ed6b1c8430075a2.jpg"),
	DEFAULT_IMAGE_2("기본 이미지 2",
		"https://eatmatebucket.s3.ap-northeast-2.amazonaws.com/ee36549fbadb7d88712f2b18cdc72eb1.jpg"),
	CUSTOM("사용자 지정", null);

	private final String description;
	private final String imageUrl;
}
