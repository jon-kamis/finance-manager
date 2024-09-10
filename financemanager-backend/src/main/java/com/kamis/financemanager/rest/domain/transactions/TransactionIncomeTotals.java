package com.kamis.financemanager.rest.domain.transactions;

import lombok.Data;

@Data
public class TransactionIncomeTotals {
    public float grossTotal;
    public float netTotal;
    public float totalPaycheck;
    public float totalBenefit;
}
