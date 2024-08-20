package com.kamis.financemanager.rest.domain.loans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CompareLoansRequest {

    private CompareLoanRequest originalLoan;
    private CompareLoanRequest newLoan;
}
