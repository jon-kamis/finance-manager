package com.kamis.financemanager.rest.domain.loans;

import lombok.Data;

import java.util.List;

@Data
public class CompareLoansResponse {
    private LoanSummaryResponse originalSummary;
    private LoanSummaryResponse compareSummary;
    private LoanSummaryResponse netSummary;
    private List<LoanPaymentComparisonResponse> paymentSchedule;
}
