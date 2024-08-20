package com.kamis.financemanager.rest.domain.loans;

import lombok.Data;

import java.util.Date;

@Data
public class LoanPaymentComparisonResponse {
    private Integer paymentNumber;
    private Date paymentDate;
    private LoanPaymentComparisonItemResponse originalPayment;
    private LoanPaymentComparisonItemResponse newPayment;
    private LoanPaymentComparisonItemResponse netPayment;
}
