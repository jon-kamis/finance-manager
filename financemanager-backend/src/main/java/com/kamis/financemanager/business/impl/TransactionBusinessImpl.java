package com.kamis.financemanager.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.TransactionFactory;
import com.kamis.financemanager.rest.domain.transactions.TransactionPostRequest;
import com.kamis.financemanager.validation.TransactionValidation;

@Component
public class TransactionBusinessImpl implements TransactionBusiness {

	@Autowired
	private TransactionValidation transactionValidation;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Override
	public boolean createTransaction(Integer userId, TransactionPostRequest request) throws FinanceManagerException {
		
		transactionValidation.validateTransactionPostRequest(userId, request);
		
		return transactionRepository.save(TransactionFactory.buildTransactionFromPostRequest(userId, request)) != null;
	}

}
