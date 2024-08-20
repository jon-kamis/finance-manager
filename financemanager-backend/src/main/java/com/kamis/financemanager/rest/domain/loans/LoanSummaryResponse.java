package com.kamis.financemanager.rest.domain.loans;

import lombok.Data;

@Data
public class LoanSummaryResponse {
    private Float interest;
    private Float payment;
    private Integer term;
}
