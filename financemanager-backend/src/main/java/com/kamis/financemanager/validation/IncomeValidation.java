package com.kamis.financemanager.validation;

import com.kamis.financemanager.exception.FinanceManagerException;

public interface IncomeValidation {

	/**
	 * Validates a GetUserIncomes request
	 * @param userId The userId of the incomes to retrieve
	 * @param sortBy Sorting parameters
	 * @param sortType Sorting direction
	 * @param page page of results to return
	 * @param pageSize size of pages to return
	 * @throws FinanceManagerException
	 */
	public void validateGetAllUserIncomesRequest(Integer userId, String sortBy, String sortType, Integer page, Integer pageSize) throws FinanceManagerException;
}
