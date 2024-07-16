package com.kamis.financemanager.validation;

import com.kamis.financemanager.exception.FinanceManagerException;

public interface FinanceManagerValidation {

	/**
	 * Validates that sort type is valid if it is present
	 * @param sortType the string to validate for sortType
	 * @throws FinanceManagerException
	 */
	public void validateSortType(String sortType) throws FinanceManagerException;
}
