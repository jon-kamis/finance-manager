package com.kamis.financemanager.enums;

public enum WeekdayEnum {

	SUNDAY("sunday"),
	MONDAY("monday"),
	TUESDAY("tuesday"),
	WEDNESDAY("wednesday"),
	THURSDAY("thursday"),
	FRIDAY("friday"),
	SATURDAY("satruday");
	
	private String weekday;
	
	WeekdayEnum(String weekday) {
		this.weekday = weekday;
	}

	public static WeekdayEnum valueOfLabel(String weekday) {
	    for (WeekdayEnum t : values()) {
	        if (t.weekday.equals(weekday)) {
	            return t;
	        }
	    }
	    return null;
	}
	
	public String getWeekday() {
		return weekday;
	}
}
