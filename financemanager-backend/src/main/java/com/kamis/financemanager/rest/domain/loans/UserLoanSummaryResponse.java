package com.kamis.financemanager.rest.domain.loans;

import lombok.Data;

@Data
public class UserLoanSummaryResponse {
    private float currentDebt;
    private float monthlyCost;
    private int count;
}
