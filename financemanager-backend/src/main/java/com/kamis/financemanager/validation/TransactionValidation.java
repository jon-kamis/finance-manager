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

	/**
	 * Validates a request to get all Transactions for a given user
	 * @param userId The user to fetch transactions for
	 * @param parentName The name of the parent table
	 * @param category Filter by category
	 * @param type Filter by type
	 * @param sortBy Sort results
	 * @param sortType direction to sort results
	 * @param page page of results to return
	 * @param pageSize pageSize of results to return
	 * @throws FinanceManagerException
	 */
	public void validateGetAllTransactionParameters(Integer userId, String parentName, String category, String type,
			String sortBy, String sortType, Integer page, Integer pageSize) throws FinanceManagerException;

	/**
	 * Validates a request to get all Transaction occurrences for a given user
	 * @param userId The user to fetch transactions for
	 * @param parent The name of the parent table
	 * @param category Filter by category
	 * @param type Filter by type
	 * @param sortBy Sort results
	 * @param sortType direction to sort results
	 * @param page page of results to return
	 * @param pageSize pageSize of results to return
	 * @throws FinanceManagerException
	 */
	public void validateGetAllTransactionOccurrenceParameters(Integer userId, String parent, String category, String type, String sortBy, String sortType, Integer page, Integer pageSize);
}
