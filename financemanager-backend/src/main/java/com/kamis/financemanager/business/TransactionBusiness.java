package com.kamis.financemanager.business;

import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.transactions.TransactionPostRequest;

public interface TransactionBusiness {

	/**
	 * Attempts to create a new transaction object based on a TransactionPostRequest
	 * @param userId The userId the new transaction will belong to
	 * @param request The request containing information for the new transaction
	 * @return true if the creation of the new transaction is successful
	 * @throws FinanceManagerException
	 */
	public boolean createTransaction(Integer userId, TransactionPostRequest request) throws FinanceManagerException;
}
