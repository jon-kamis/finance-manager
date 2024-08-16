package com.kamis.financemanager.rest.domain.transactions;

import lombok.Data;

import java.util.List;

@Data
public class PagedTransactionOccurrenceResponse {

	private int count;
	private int page;
	private int pageSize;
	private List<TransactionOccuranceResponse> items;
}
