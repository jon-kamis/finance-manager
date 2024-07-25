package com.kamis.financemanager.validation;

import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.transactions.TransactionPostRequest;

public interface TransactionValidation {

	/**
	 * Validates a request for a new transaction is valid and throws an exception if it is not
	 * @param userId the UserId of the user a transaction is being generated for
	 * @param request The request to validate
	 * @throws FinanceManagerException
	 */
	public void validateTransactionPostRequest(Integer userId, TransactionPostRequest request) throws FinanceManagerException;
}
