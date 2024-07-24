package com.kamis.financemanager.enums;

public enum TransactionTypeEnum {
	INCOME("income"),
	EXPENSE("expense");

	private String type;
	
	TransactionTypeEnum(String type) {
		this.type = type;
	}
	
	public static TransactionTypeEnum valueOfLabel(String type) {
	    for (TransactionTypeEnum t : values()) {
	        if (t.type.equals(type)) {
	            return t;
	        }
	    }
	    return null;
	}
	
	public String getType() {
		return type;
	}
}
