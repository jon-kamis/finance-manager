package com.kamis.financemanager.rest.domain.transactions;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TransactionOccuranceResponse {
    private int transactionId;
    private String name;
    private String type;
    private String category;
    private float amount;
    private Date date;
}
