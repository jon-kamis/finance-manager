package com.kamis.financemanager.constants;

import java.util.List;

import com.kamis.financemanager.enums.TableNameEnum;

import io.jsonwebtoken.lang.Arrays;

public class FinanceManagerConstants {
	
	//Loan Constants
	public static final String LOAN_SORT_BY_NAME = "name";
	public static final String LOAN_SORT_BY_FIRST_PAYMENT_DATE = "firstPaymentDate";
	public static final String LOAN_SORT_BY_BALANCE = "balance";
	public static final List<String> LOAN_VALID_SORT_TYPES = Arrays.asList(new String[] {LOAN_SORT_BY_NAME, LOAN_SORT_BY_FIRST_PAYMENT_DATE, LOAN_SORT_BY_BALANCE});
	
	//Transaction Constants "amount", "category", "name", "type"
	public static final String TRANSACTION_SORT_BY_AMOUNT = "amount";
	public static final String TRANSACTION_SORT_BY_CATEGORY = "category";
	public static final String TRANSACTION_SORT_BY_NAME = "name";
	public static final String TRANSACTION_SORT_BY_TYPE = "type";
	public static final List<String> TRANSACTION_VALID_SORT_OPTIONS = Arrays.asList(new String[] {TRANSACTION_SORT_BY_AMOUNT, TRANSACTION_SORT_BY_CATEGORY, 
			TRANSACTION_SORT_BY_NAME, TRANSACTION_SORT_BY_TYPE});
	
	//Income constants
	public static final String TAX_NAME_FEDERAL = "Federal tax";
	public static final String TAX_NAME_SOC_SEC = "Social Security";
	public static final String TAX_NAME_MEDICARE = "Medicare";
	public static final String TAX_NAME_PA_STATE = "Pennsylvania Income tax";
	public static final String TAX_NAME_PA_UNEMP = "Pennsylvania Unemployment Insurance";
	public static final String TAX_NAME_LOCAL = "Local Income tax";
	public static final String INCOME_SORT_BY_NAME = "name";
	public static final String INCOME_SORT_BY_AMOUNT = "amount";
	public static final List<String> INCOME_VALID_SORT_OPTIONS = Arrays.asList(new String[] {INCOME_SORT_BY_NAME, INCOME_SORT_BY_AMOUNT});
	
	//Sorting constants
	public static final String SORT_TYPE_ASC = "asc";
	public static final String SORT_TYPE_DESC = "desc";
	public static final List<String> VALID_SORT_TYPES = Arrays.asList(new String[] {SORT_TYPE_ASC, SORT_TYPE_DESC});
	
	//Role constants
	public static final String ADMIN_ROLE = "admin";
	
	//JWT Constants
	public static final String JWT_CLAIM_USER_ID = "userId";
	public static final String JWT_CLAIM_USER_DISPLAY_NAME = "displayName";
	public static final String JWT_CLAIM_USER_FIRST_NAME = "firstName";
	public static final String JWT_CLAIM_USER_ROLES = "userRoles";

}
