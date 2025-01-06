package com.example.eatmate.app.domain.report.converter;

import java.io.IOException;
import java.util.List;

import com.example.eatmate.app.domain.report.domain.ReportType;
import com.example.eatmate.global.config.error.ErrorCode;
import com.example.eatmate.global.config.error.exception.CommonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;

public class ReportTypeListConverter implements AttributeConverter<List<ReportType>, String> {
	private static final ObjectMapper mapper = new ObjectMapper()
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

	@Override
	public String convertToDatabaseColumn(List<ReportType> attribute) {
		try {
			return mapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new CommonException(ErrorCode.INVALID_REPORT_TYPE_LIST);
		}
	}

	@Override
	public List<ReportType> convertToEntityAttribute(String dbData) {
		try {
			return mapper.readValue(dbData, new TypeReference<>() {
			});
		} catch (IOException e) {
			throw new IllegalArgumentException();
		}
	}
}
