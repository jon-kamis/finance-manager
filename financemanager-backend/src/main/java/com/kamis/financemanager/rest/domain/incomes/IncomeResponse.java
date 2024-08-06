package com.kamis.financemanager.rest.domain.incomes;

import java.util.Date;
import java.util.List;

import com.kamis.financemanager.rest.domain.transactions.TransactionResponse;

import lombok.Data;

@Data
public class IncomeResponse {
	
	private Integer id;
	private Integer userId;
	private String name;
	private Float withheldTax;
	private Integer taxCredits;
	private Date createDate;
	private Date lastUpdateDate;
	private String lastUpdateBy;
	private String category;
	private String frequency;
	private Float amount;
	private Float taxes;
	private List<TransactionResponse> transactions;
}
