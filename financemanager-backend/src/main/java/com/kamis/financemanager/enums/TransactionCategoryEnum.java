package com.kamis.financemanager.enums;

public enum TransactionCategoryEnum {
	BILL("bill"),
	PAYCHECK("paycheck"),
	LOAN_PAYMENT("loan-payment");

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
