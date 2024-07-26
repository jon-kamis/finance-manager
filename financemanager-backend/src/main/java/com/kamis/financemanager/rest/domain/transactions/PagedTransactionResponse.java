package com.kamis.financemanager.rest.domain.transactions;

import java.util.List;

import lombok.Data;

@Data
public class PagedTransactionResponse {

	private int count;
	private int page;
	private int pageSize;
	private List<TransactionResponse> items;
}
