package com.kamis.financemanager.enums;

public enum StateEnum {
	
	ALABAMA("AL", false),
	ALASKA("AK", false),
	ARIZONA("AZ", false),
	ARKANSAS("AR", false),
	CALIFORNIA("CA", false),
	COLORADO("CO", false),
	CONNECTICUT("CT", false),
	DELAWARE("DE", false),
	DISTRICT_OF_COLUMBIA("DC", false),
	FLORIDA("FL", false),
	GEORGIA("GA", false),
	HAWAII("HI", false),
	IDAHO("ID", false),
	ILLINOIS("IL", false),
	INDIANA("IN", false),
	IOWA("IA", false),
	KANSAS("KS", false),
	KENTUCKY("KY", false),
	LOUISIANA("LA", false),
	MAINE("ME", false),
	MARYLAND("MD", false),
	MASSACHUSETTS("MA", false),
	MICHIGAN("MI", false),
	MINNESOTA("MN", false),
	MISSISSIPPI("MS", false),
	MISSOURI("MO", false),
	MONTANA("MT", false),
	NEBRASKA("NE", false),
	NEVADA("NV", false),
	NEW_HAMPSHIRE("NH", false),
	NEW_JERSEY("NJ", false),
	NEW_MEXICO("NM", false),
	NEW_YORK("NY", false),
	NORTH_CAROLINA("NC", false),
	NORTH_DAKOTA("ND", false),
	OHIO("OH", false),
	OKLAHOMA("OK", false),
	OREGON("OR", false),
	PENNSYLVANIA("PA", true),
	RHODE_ISLAND("RI", false),
	SOUTH_CAROLINA("SC", false),
	SOUTH_DAKOTA("SD", false),
	TENNESSEE("TN", false),
	TEXAS("TX", false),
	UTAH("UT", false),
	VERMONT("VT", false),
	VIRGINIA("VA", false),
	WASHINGTON("WA", false),
	WEST_VIRGINIA("WV", false),
	WISCONSIN("WI", false),
	WYOMING("WY", false);
	
	private String state;
	private boolean taxEnabled;
	
	StateEnum(String state, boolean taxEnabled) {
		this.state = state;
		this.taxEnabled = taxEnabled;
	}
	
	public static StateEnum valueOfLabel(String state) {
	    for (StateEnum t : values()) {
	        if (t.getState().equals(state)) {
	            return t;
	        }
	    }
	    return null;
	}

	public String getState() {
		return state;
	}

	public boolean isTaxEnabled() {
		return taxEnabled;
	}
}
