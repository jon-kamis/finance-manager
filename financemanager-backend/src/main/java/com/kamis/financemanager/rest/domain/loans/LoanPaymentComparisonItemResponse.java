package com.kamis.financemanager.rest.domain.loans;

import lombok.Data;

@Data
public class LoanPaymentComparisonItemResponse {
    private float principal;
    private float principalToDate;
    private float interest;
    private float interestToDate;
    private float amount;
    private float balance;
}
