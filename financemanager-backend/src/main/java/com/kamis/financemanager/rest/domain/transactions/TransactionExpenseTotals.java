package com.kamis.financemanager.rest.domain.transactions;

import lombok.Data;

@Data
public class TransactionExpenseTotals {
    public float totalExpense;
    public float totalBenefit;
    public float totalTax;
    public float totalBills;
    public float totalLoanPayments;
    public float totalMisc;
}
