package com.kamis.financemanager.validation;

import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.loans.CompareLoansRequest;
import com.kamis.financemanager.rest.domain.loans.LoanRequest;
import com.kamis.financemanager.rest.domain.loans.ManualLoanPaymentRequest;

public interface LoanValidation {

	/**
	 * Validates all fields for a get all user loans request are valid
	 * @param userId The userId to validate
	 * @param sortBy The sortBy parameter to validate
	 * @param sortType The sortType parameter to validate
	 * @throws FinanceManagerException
	 */
	public void validateGetAllLoansRequest(Integer userId, String sortBy, String sortType) throws FinanceManagerException;

	/**
	 * Validates all fields from a loan request
	 * @param request The request to validate
	 * @throws FinanceManagerException
	 */
    void validateLoanRequest(LoanRequest request) throws FinanceManagerException;

	/**
	 * Validates a CompareLoanRequest
	 * @param request the request to validate
	 * @throws FinanceManagerException
	 */
	void validateCompareLoanRequest(CompareLoansRequest request) throws FinanceManagerException;

	/**
	 * Validates a ManualLoanPaymentRequest
	 * @param loanId The id of the loan to validate the request for
	 * @param request The request to validate
	 */
    void validateCreatePaymentRequest(int loanId, ManualLoanPaymentRequest request);

}
