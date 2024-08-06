package com.kamis.financemanager.rest.domain.incomes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class IncomeSummaryItem {
	private String name;
	private Float amount;

	@Schema(allowableValues = {"income", "expense"})
	private String type;
	
	@Schema(allowableValues = {"benefit", "paycheck", "taxes"})
	private String category;
	
}
