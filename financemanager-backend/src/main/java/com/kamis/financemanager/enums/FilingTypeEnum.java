package com.kamis.financemanager.enums;

public enum FilingTypeEnum {

	SINGLE("single"),
	MARRIED_FILING_JOINTLY("married-filing-jointly"),
	MARRIED_FILING_SEPARATELY("married-filing-separately"),
	HEAD_OF_HOUSEHOLD("head-of-household");
	
	private String filingType;
	
	public static FilingTypeEnum valueOfLabel(String filingType) {
	    for (FilingTypeEnum f : values()) {
	        if (f.filingType.equals(filingType)) {
	            return f;
	        }
	    }
	    return null;
	}
	
	FilingTypeEnum(String filingType) {
		this.filingType = filingType;
	}

	public String getFilingType() {
		return filingType;
	}
	
}
