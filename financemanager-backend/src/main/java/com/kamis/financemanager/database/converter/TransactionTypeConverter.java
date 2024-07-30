package com.kamis.financemanager.database.converter;

import java.util.stream.Stream;

import com.kamis.financemanager.enums.TransactionTypeEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<TransactionTypeEnum, String>{

	@Override
	public String convertToDatabaseColumn(TransactionTypeEnum attribute) {
		
		if (attribute == null) {
			return null;
		}
		
		return attribute.getType();
	}

	@Override
	public TransactionTypeEnum convertToEntityAttribute(String dbData) {
		
		return Stream.of(TransactionTypeEnum.values())
				.filter(t -> t.getType().equals(dbData))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
