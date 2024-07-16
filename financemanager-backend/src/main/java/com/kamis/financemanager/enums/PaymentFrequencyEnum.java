package com.kamis.financemanager.enums;

public enum PaymentFrequencyEnum {

	MONTHLY("monthly"),
	BIWEEKLY("bi-weekly"),
	WEEKLY("weekly");

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
