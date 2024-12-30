package com.example.eatmate.app.domain.meeting.domain;

import lombok.Getter;

@Getter
public enum FoodCategory {
	BURGER("버거"),
	JOKBAL("족발/보쌈"),
	PIZZA("피자"),
	JAPANESE("일식"),
	CUTLET("돈까스"),
	CHINESE("중식"),
	KOREAN("구이"),
	CHICKEN("치킨"),
	ASIAN("아시안"),
	DESSERT("디저트"),
	CAFE("카페/차"),
	WESTERN("양식"),
	SUSHI("회/해물");

	private final String description;

	FoodCategory(String description) {
		this.description = description;
	}
}
