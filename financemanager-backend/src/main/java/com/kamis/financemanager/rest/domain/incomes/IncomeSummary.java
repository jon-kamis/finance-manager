package com.kamis.financemanager.rest.domain.incomes;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IncomeSummary {
	public Float totalIncome;
	public Float totalTax;
	public List<IncomeSummaryItem> items;
	
	public IncomeSummary() {
		items = new ArrayList<>();
		totalIncome = (float)0;
		totalTax = (float)0;
	}
}
