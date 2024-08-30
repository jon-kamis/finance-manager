package com.kamis.financemanager.rest.domain.loans;

import lombok.Data;

import java.util.Date;

@Data
public class LoanManualPaymentResponse {

    private int id;
    private float amount;
    private Date effectiveDate;
    private Date expirationDate;
}
