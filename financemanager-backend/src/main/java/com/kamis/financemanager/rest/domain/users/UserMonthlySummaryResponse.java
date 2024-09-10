package com.kamis.financemanager.rest.domain.users;

import com.kamis.financemanager.rest.domain.transactions.TransactionExpenseTotals;
import com.kamis.financemanager.rest.domain.transactions.TransactionIncomeTotals;
import com.kamis.financemanager.rest.domain.transactions.TransactionOccuranceResponse;
import lombok.Data;

import java.util.List;

@Data
public class UserMonthlySummaryResponse {

    public String month;
    private TransactionIncomeTotals incomeTotals;
    private TransactionExpenseTotals expenseTotals;
    public List<TransactionOccuranceResponse> transactions;

}
