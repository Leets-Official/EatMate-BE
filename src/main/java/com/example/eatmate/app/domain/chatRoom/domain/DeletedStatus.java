package com.example.eatmate.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeletedStatus {
	DELETED("DELETED"),
	NOT_DELETED("NOT_DELETED");

	private final String deletedStatus;
}
