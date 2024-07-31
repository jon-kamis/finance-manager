package com.kamis.financemanager.database.converter;

import java.util.stream.Stream;

import com.kamis.financemanager.enums.TransactionCategoryEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionCategoryConverter implements AttributeConverter<TransactionCategoryEnum, String>{

	@Override
	public String convertToDatabaseColumn(TransactionCategoryEnum attribute) {
		
		if (attribute == null) {
			return null;
		}
		
		return attribute.getCategory();
	}

	@Override
	public TransactionCategoryEnum convertToEntityAttribute(String dbData) {
		
		if (dbData == null || dbData.isBlank()) {
			return null;
		}
		
		return Stream.of(TransactionCategoryEnum.values())
				.filter(c -> c.getCategory().equals(dbData))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
