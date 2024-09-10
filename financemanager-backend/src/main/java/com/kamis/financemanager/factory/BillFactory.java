package com.kamis.financemanager.factory;

import com.kamis.financemanager.database.domain.Bill;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.rest.domain.bills.BillPostRequest;
import com.kamis.financemanager.util.FinanceManagerUtil;

public class BillFactory {

    /**
     * Creates a new Bill from a BillPostRequest
     * @param request The request containing the details for the new bill
     * @param userId the id of the user who owns the new bill
     * @return A new Bill object created from the request and userId
     */
    public static Bill createBill(BillPostRequest request, int userId) {
        Bill bill = new Bill();
        bill.setName(request.getName());
        bill.setCategory(TransactionCategoryEnum.valueOfLabel(request.getCategory()));
        bill.setFrequency(PaymentFrequencyEnum.valueOfLabel(request.getFrequency()));
        bill.setAmount(request.getAmount());
        bill.setUserId(userId);
        bill.setAuditInfo(FinanceManagerUtil.getAuditInfo());

        return bill;
    }
}
