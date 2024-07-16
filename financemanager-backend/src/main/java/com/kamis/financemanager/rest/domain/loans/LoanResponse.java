package com.kamis.financemanager.rest.domain.loans;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class LoanResponse {

	private Integer id;
    private Integer userId;
    private String name;
    private Float principal;
    private Date firstPaymentDate;
    private String frequency;
    private Float interest;
    private Float payment;
    private Float rate;
    private Integer term;
    private Float balance;
	private List<LoanPaymentItemResponse> paymentSchedule;
}
