package com.kamis.financemanager.validation;

import com.kamis.financemanager.exception.FinanceManagerException;

public interface LoanValidation {

	/**
	 * Validates all fields for a get all user loans request are valid
	 * @param userId The userId to validate
	 * @param name The name to validate
	 * @param sortBy The sortBy parameter to validate
	 * @param sortType The sortType parameter to validate
	 * @throws FinanceManagerException
	 */
	public void validateGetAllLoansRequest(Integer userId, String sortBy, String sortType) throws FinanceManagerException;
}
