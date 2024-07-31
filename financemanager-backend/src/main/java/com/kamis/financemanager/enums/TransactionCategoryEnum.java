package com.kamis.financemanager.enums;

public enum TransactionCategoryEnum {
	BILL("bill"),
	BENEFIT("benefit"),
	PAYCHECK("paycheck"),
	LOAN("loan-payment"),
	TAXES("taxes");

	private String category;
	
	TransactionCategoryEnum(String type) {
		this.category = type;
	}
	
	public static TransactionCategoryEnum valueOfLabel(String category) {
	    for (TransactionCategoryEnum t : values()) {
	        if (t.category.equals(category)) {
	            return t;
	        }
	    }
	    return null;
	}
	
	public String getCategory() {
		return category;
	}
}
