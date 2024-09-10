package com.kamis.financemanager.business.impl;

import com.kamis.financemanager.business.BillBusiness;
import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.database.domain.Bill;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.repository.BillRepository;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.enums.TableNameEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.BillFactory;
import com.kamis.financemanager.factory.TransactionFactory;
import com.kamis.financemanager.rest.domain.bills.BillPostRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BillBusinessImpl implements BillBusiness {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionBusiness transactionBusiness;

    @Override
    @Transactional
    public boolean createBill(Integer userId, BillPostRequest request) throws FinanceManagerException {

        //TODO: Validate request

        Bill bill = BillFactory.createBill(request, userId);
        bill = billRepository.saveAndFlush(bill);

        Transaction transaction = TransactionFactory.buildTransactionFromBillPostRequest(request, userId);
        transaction.setParentTableName(TableNameEnum.BILLS);
        transaction.setParentId(bill.getId());

        transactionRepository.save(transaction);

        return true;
    }
}
