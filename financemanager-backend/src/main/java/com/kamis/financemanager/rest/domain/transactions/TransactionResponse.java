package com.kamis.financemanager.rest.domain.transactions;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class TransactionResponse {

	private int id;
	private String name;
	private String frequency;
	private String type;
	private String category;
	private float amount;
	private List<TransactionDayResponse> transactionDays;
	private Date effectiveDate;
	private Date expirationDate;
	
}
