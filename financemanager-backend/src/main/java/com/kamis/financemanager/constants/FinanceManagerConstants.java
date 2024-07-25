package com.kamis.financemanager.constants;

import java.util.List;

import io.jsonwebtoken.lang.Arrays;

public class FinanceManagerConstants {
	public static final String LOAN_SORT_BY_NAME = "name";
	public static final String LOAN_SORT_BY_FIRST_PAYMENT_DATE = "firstPaymentDate";
	public static final String LOAN_SORT_BY_BALANCE = "balance";
	public static final List<String> LOAN_VALID_SORT_TYPES = Arrays.asList(new String[] {LOAN_SORT_BY_NAME, LOAN_SORT_BY_FIRST_PAYMENT_DATE, LOAN_SORT_BY_BALANCE});
	
	//Sorting constants
	public static final String SORT_TYPE_ASC = "asc";
	public static final String SORT_TYPE_DESC = "desc";
	
	//Role constants
	public static final String ADMIN_ROLE = "admin";
}
