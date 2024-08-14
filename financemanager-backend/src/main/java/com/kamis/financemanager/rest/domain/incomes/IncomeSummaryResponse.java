package com.kamis.financemanager.rest.domain.incomes;

import java.util.List;

import lombok.Data;

@Data
public class IncomeSummaryResponse {
	private Integer userId;
	private String month;
	private IncomeSummary annualSummary;
	private IncomeSummary monthSummary;
	private List<IncomeMonth> forecastedEarnings;
}
