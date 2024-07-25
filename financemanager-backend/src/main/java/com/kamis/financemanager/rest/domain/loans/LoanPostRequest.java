package com.kamis.financemanager.rest.domain.loans;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoanPostRequest {
	
	private String name;
	
	private Integer term;
	
	private Date firstPaymentDate;
	
	private Float principal;
	
	private Float rate;

	@Schema(allowableValues= {"monthly", "bi-weekly", "weekly"})
	private String frequency;
}
