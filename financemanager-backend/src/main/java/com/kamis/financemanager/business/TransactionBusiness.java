package com.kamis.financemanager.business;

import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.transactions.PagedTransactionResponse;
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

	/**
	 * Retrieves all User Transactions meeting criteria
	 * @param userId The id of the user to retrieve transactions for
	 * @param name Filter results by name
	 * @param category Filter results by category
	 * @param type Filter results by type
	 * @param sortBy Sort results
	 * @param sortType Direction of sorting
	 * @param page Page of results to return
	 * @param pageSize PageSize of results to return
	 * @return A PagedTransactionReponse containing all user transactions meeting the given criteria
	 */
	public PagedTransactionResponse getAllUserTransactions(Integer userId, String name, String category, String type,
			String sortBy, String sortType, Integer page, Integer pageSize) throws FinanceManagerException;
}