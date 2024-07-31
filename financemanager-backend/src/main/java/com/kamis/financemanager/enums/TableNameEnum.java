package com.kamis.financemanager.enums;

public enum TableNameEnum {

	INCOMES("incomes"),
	LOANS("loans"),
	BILLS("bills");
	
	private String name;
	
	TableNameEnum(String name) {
		this.name = name;
	}

	public static TableNameEnum valueOfLabel(String tableName) {
	    for (TableNameEnum t : values()) {
	        if (t.getName().equals(tableName)) {
	            return t;
	        }
	    }
	    return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
