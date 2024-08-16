package com.kamis.financemanager.enums;

import lombok.Getter;

@Getter
public enum TableNameEnum {

	INCOMES("incomes"),
	LOANS("loans"),
	BILLS("bills");
	
	private final String name;
	
	TableNameEnum(String name) {
		this.name = name;
	}

	public static TableNameEnum valueOfLabel(String tableName) {
	    for (TableNameEnum t : values()) {
	        if (t.getName().equalsIgnoreCase(tableName)) {
	            return t;
	        }
	    }
	    return null;
	}

}
