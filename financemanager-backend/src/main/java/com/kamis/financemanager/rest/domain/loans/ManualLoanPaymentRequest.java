package com.kamis.financemanager.rest.domain.loans;

import lombok.Data;

import java.util.Date;

@Data
public class ManualLoanPaymentRequest {
    private Float amount;
    private Date effectiveDate;
    private Date expirationDate;
}
