package com.kamis.financemanager.rest.domain.users;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DebtTotals {
    public float creditCards;
    public float loans;

    public DebtTotals() {
        creditCards = (float)0;
        loans = (float)0;
    }
}
