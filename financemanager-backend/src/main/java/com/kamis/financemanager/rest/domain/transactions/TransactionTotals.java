package com.kamis.financemanager.rest.domain.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionTotals {
    public String month;
    public float totalIncome;
    public float totalTax;
    public float totalBills;
    public float totalLoanPayments;
    public float totalMisc;

    public TransactionTotals() {
        month = "";
    }
}
