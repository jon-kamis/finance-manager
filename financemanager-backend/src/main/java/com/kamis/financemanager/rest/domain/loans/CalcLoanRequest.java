package com.kamis.financemanager.rest.domain.loans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CalcLoanRequest {

    private Integer term;

    private Date firstPaymentDate;

    private Float principal;

    private Float rate;

    @Schema(allowableValues= {"monthly", "semi-monthly", "bi-weekly", "weekly"})
    private String frequency;
}
