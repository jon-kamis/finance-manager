package com.kamis.financemanager.rest.domain.bills;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BillPostRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"bill", "benefit"})
    private String category;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"weekly", "bi-weekly", "semi-monthly", "monthly", "quarterly", "semi-annual", "annual"})
    private String frequency;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "amount per transaction")
    private Float amount;

    @Schema(description = "Day of week for bill. Only applicable for 'weekly' frequencies")
    private String weekday;

    @Schema(description = "First billing date. Applicable for all pay types other than weekly, monthly, and semi-monthly")
    private Date startDate;

    @Schema(description = "Billing days for the month. Only required when frequency is semi-monthly or monthly")
    private List<Integer> daysOfMonth;

    private Date effectiveDate;

    private Date expirationDate;
}
