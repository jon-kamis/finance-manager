package com.kamis.financemanager.rest.domain.transactions;

import java.util.Date;

import lombok.Data;

@Data
public class TransactionDayResponse {

	public Integer day;
	public String weekday;
	public Date startDate;
}
