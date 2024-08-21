package com.kamis.financemanager.rest.domain.loans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CompareLoansRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"monthly", "semi-monthly", "weekly", "bi-weekly"})
    private String frequency;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "YYYY-MM-DD")
    private Date firstPaymentDate;

    private CompareLoanRequest originalLoan;
    private CompareLoanRequest newLoan;
}
