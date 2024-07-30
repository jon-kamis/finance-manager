package com.kamis.financemanager.enums;

public enum PaymentFrequencyEnum {

	MONTHLY("monthly", 12),
	SEMI_MONTHLY("semi-monthly", 24),
	BIWEEKLY("bi-weekly", 26),
	WEEKLY("weekly", 52),
	QUARTERLY("quarterly", 4),
	ANNUAL("annual", 1);

	private String frequency;
	private int numPays;
	
	PaymentFrequencyEnum(String frequency, int numPays) {
		this.frequency = frequency;
		this.numPays = numPays;
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
	
	public int getNumPays() {
		return numPays;
	}
		
}
