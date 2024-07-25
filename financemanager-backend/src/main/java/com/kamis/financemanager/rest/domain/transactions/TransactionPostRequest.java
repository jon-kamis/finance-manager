package com.kamis.financemanager.rest.domain.transactions;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPostRequest {

	private String name;
	
	@Schema(allowableValues= {"income", "expense"})
	private String type;
	
	private String category;
	
	private String frequency;
	
	private Float amount;
	
	private List<Integer> daysOfMonth;
	
	private Date effectiveDate;
	
	private Date expirationDate;
	
}
