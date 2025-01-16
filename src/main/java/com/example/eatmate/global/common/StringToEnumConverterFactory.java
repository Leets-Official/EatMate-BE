package com.example.eatmate.global.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

	@Override
	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToEnumConverter<>(targetType);
	}

	private static class StringToEnumConverter<T extends Enum<T>> implements Converter<String, T> {

		private final Class<T> enumType;

		public StringToEnumConverter(Class<T> enumType) {
			this.enumType = enumType;
		}

		@Override
		public T convert(String source) {
			String formattedSource = source.replace("-", "_").toUpperCase();
			return Enum.valueOf(enumType, formattedSource);
		}
	}
}
