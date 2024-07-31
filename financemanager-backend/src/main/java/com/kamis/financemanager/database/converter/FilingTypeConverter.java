package com.kamis.financemanager.database.converter;

import java.util.stream.Stream;

import com.kamis.financemanager.enums.FilingTypeEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FilingTypeConverter implements AttributeConverter<FilingTypeEnum, String>{

	@Override
	public String convertToDatabaseColumn(FilingTypeEnum attribute) {
		
		if (attribute == null) {
			return null;
		}
		
		return attribute.getFilingType();
	}

	@Override
	public FilingTypeEnum convertToEntityAttribute(String dbData) {
		
		if (dbData == null || dbData.isBlank()) {
			return null;
		}
		
		return Stream.of(FilingTypeEnum.values())
				.filter(w -> w.getFilingType().equals(dbData))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
