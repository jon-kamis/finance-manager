package com.kamis.financemanager.database.converter;

import java.util.stream.Stream;

import com.kamis.financemanager.enums.FilingTypeEnum;
import com.kamis.financemanager.enums.StateEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StateConverter implements AttributeConverter<StateEnum, String>{

	@Override
	public String convertToDatabaseColumn(StateEnum attribute) {
		
		if (attribute == null) {
			return null;
		}
		
		return attribute.getState();
	}

	@Override
	public StateEnum convertToEntityAttribute(String dbData) {
		
		if (dbData == null || dbData.isBlank()) {
			return null;
		}
		
		return Stream.of(StateEnum.values())
				.filter(w -> w.getState().equals(dbData))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
