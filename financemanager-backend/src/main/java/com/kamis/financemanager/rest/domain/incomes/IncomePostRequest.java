package com.kamis.financemanager.rest.domain.incomes;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomePostRequest {

private String name;

	@Schema(description = "Withheld tax per pay")
	private Float taxWithheld;
	
	@Schema(description = "Tax credits")
	private Integer taxCredits;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"paycheck", "benefit"})
	private String category;
	
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"weekly", "bi-weekly", "semi-monthly", "monthly"})
	private String frequency;
	
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "gross pay for the month")
	private Float amount;
	
	@Schema(description = "Day of week for payday. Only applicable for 'weekly' pay frequencies")
	private String weekday;
	
	@Schema(description = "First pay date. Only applicable for 'bi-weekly' pay frequencies")
	private Date startDate;
	
	@Schema(description = "Paydays for the month. Only required when frequency is semi-monthly or monthly")
	private List<Integer> daysOfMonth;
	
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "determines if tax should be generated for the new income")
	private Boolean taxable;
	
	private Date effectiveDate;
	
	private Date expirationDate;
}
