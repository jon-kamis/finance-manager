package com.kamis.financemanager.database.converter;

import java.util.stream.Stream;

import com.kamis.financemanager.enums.PaymentFrequencyEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentFrequencyConverter implements AttributeConverter<PaymentFrequencyEnum, String>{

	@Override
	public String convertToDatabaseColumn(PaymentFrequencyEnum attribute) {
		
		if (attribute == null) {
			return null;
		}
		
		return attribute.getFrequency();
	}

	@Override
	public PaymentFrequencyEnum convertToEntityAttribute(String dbData) {
		
		if (dbData == null || dbData.isBlank()) {
			return null;
		}
		
		return Stream.of(PaymentFrequencyEnum.values())
				.filter(p -> p.getFrequency().equals(dbData))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
