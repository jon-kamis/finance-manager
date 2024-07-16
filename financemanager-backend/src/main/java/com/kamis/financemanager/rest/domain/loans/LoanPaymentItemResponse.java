package com.kamis.financemanager.rest.domain.loans;

import java.util.Date;

import lombok.Data;

@Data
public class LoanPaymentItemResponse {

	private Integer id;
    private Integer paymentNumber;
    private Date paymentDate;
    private Float principal;
    private Float principalToDate;
    private Float interest;
    private Float interestToDate;
    private Float amount;
    private Float balance;
    
}
