package com.kamis.financemanager.enums;

public enum TableNameEnum {

	INCOMES("incomes"),
	LOANS("loans"),
	BILLS("bills");
	
	private String name;
	
	TableNameEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
