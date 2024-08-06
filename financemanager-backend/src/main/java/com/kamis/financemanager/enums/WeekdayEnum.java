package com.kamis.financemanager.enums;

public enum WeekdayEnum {

	MONDAY("monday", 1),
	TUESDAY("tuesday", 2),
	WEDNESDAY("wednesday", 3),
	THURSDAY("thursday", 4),
	FRIDAY("friday", 5),
	SATURDAY("satruday", 6),
	SUNDAY("sunday", 7);
	
	private String weekday;
	private int dayIndex;
	
	WeekdayEnum(String weekday, int dayIndex) {
		this.weekday = weekday;
		this.dayIndex = dayIndex;
	}

	public static WeekdayEnum valueOfLabel(String weekday) {
	    for (WeekdayEnum t : values()) {
	        if (t.weekday.equals(weekday)) {
	            return t;
	        }
	    }
	    return null;
	}
	
	public int getDayIndex() {
		return dayIndex;
	}
	
	public String getWeekday() {
		return weekday;
	}
}
