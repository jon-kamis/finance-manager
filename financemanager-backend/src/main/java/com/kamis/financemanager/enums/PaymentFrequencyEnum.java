package com.kamis.financemanager.enums;

public enum PaymentFrequencyEnum {

	MONTHLY("monthly"),
	SEMI_MONTHLY("semi-monthly"),
	BIWEEKLY("bi-weekly"),
	WEEKLY("weekly"),
	QUARTERLY("quarterly"),
	ANNUAL("annual");

	private String frequency;
	
	PaymentFrequencyEnum(String frequency) {
		this.frequency = frequency;
	}
	
	public static PaymentFrequencyEnum valueOfLabel(String frequency) {
	    for (PaymentFrequencyEnum f : values()) {
	        if (f.frequency.equals(frequency)) {
	            return f;
	        }
	    }
	    return null;
	}
	
	public String getFrequency() {
		return frequency;
	}
		
}
