package com.kamis.financemanager.rest.domain.users;

import com.kamis.financemanager.rest.domain.transactions.TransactionOccuranceResponse;
import com.kamis.financemanager.rest.domain.transactions.TransactionTotals;
import lombok.Data;

import java.util.List;

@Data
public class UserMonthlySummaryResponse {

    public TransactionTotals totals;
    public List<TransactionOccuranceResponse> transactions;

}
