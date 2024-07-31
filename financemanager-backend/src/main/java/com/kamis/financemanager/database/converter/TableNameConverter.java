package com.kamis.financemanager.database.converter;

import java.util.stream.Stream;

import com.kamis.financemanager.enums.TableNameEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TableNameConverter implements AttributeConverter<TableNameEnum, String>{

	@Override
	public String convertToDatabaseColumn(TableNameEnum attribute) {
		
		if (attribute == null) {
			return null;
		}
		
		return attribute.getName();
	}

	@Override
	public TableNameEnum convertToEntityAttribute(String dbData) {
		
		if (dbData == null || dbData.isBlank()) {
			return null;
		}
		
		return Stream.of(TableNameEnum.values())
				.filter(w -> w.getName().equals(dbData))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
