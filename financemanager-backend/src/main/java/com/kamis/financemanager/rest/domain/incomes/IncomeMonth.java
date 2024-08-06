package com.kamis.financemanager.rest.domain.incomes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeMonth {
	private int month;
	private String monthLabel;
	private float grossIncome;
	private float netIncome;
	private float tax;
}
