package com.kamis.financemanager.rest.domain.loans;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class CompareLoanRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer term;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Float principal;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Float rate;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Float payment;
}
