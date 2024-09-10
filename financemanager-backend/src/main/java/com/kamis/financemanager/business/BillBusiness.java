package com.kamis.financemanager.business;

import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.bills.BillPostRequest;

public interface BillBusiness {

    /**
     * Attempts to create a new bill for a user
     * @param userId The user to create the bill for
     * @param request The request containing the details for the new bill
     * @return true if the bill is created successfully
     * @throws FinanceManagerException
     */
    boolean createBill(Integer userId, BillPostRequest request) throws FinanceManagerException;
}
