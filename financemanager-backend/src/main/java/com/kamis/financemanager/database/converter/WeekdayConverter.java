package com.kamis.financemanager.database.converter;

import java.util.stream.Stream;

import com.kamis.financemanager.enums.WeekdayEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WeekdayConverter implements AttributeConverter<WeekdayEnum, String>{

	@Override
	public String convertToDatabaseColumn(WeekdayEnum attribute) {
		
		if (attribute == null) {
			return null;
		}
		
		return attribute.getWeekday();
	}

	@Override
	public WeekdayEnum convertToEntityAttribute(String dbData) {
		
		if (dbData == null || dbData.isBlank()) {
			return null;
		}
		
		return Stream.of(WeekdayEnum.values())
				.filter(w -> w.getWeekday().equals(dbData))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
