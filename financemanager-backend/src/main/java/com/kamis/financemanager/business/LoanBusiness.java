package com.kamis.financemanager.business;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.loans.LoanPostRequest;

public interface LoanBusiness {

	/**
	 * Attempts to create a new Loan for a user
	 * @param request A request containing the loan information
	 * @param userId The id of the user to create the loan for
	 * @return true if the loan was created succesfully
	 * @throws FinanceManagerException
	 */
	public boolean createLoan(LoanPostRequest request, Integer userId) throws FinanceManagerException;

	/**
	 * Attempts to perform payment calculations on a new Loan object
	 * @param loan The loan to perform the payment calculations on
	 * @return The loan with payment fields populated
	 * @throws FinanceManagerException
	 */
	public Loan calculateLoanPament(Loan loan) throws FinanceManagerException;
	
	/**
	 * Calculates the payment schedule for a loan
	 * @param loan The loan to perform the payment schedule calculation on
	 * @return The loan with payment schedule list filled
	 * @throws FinanceManagerException
	 */
	public Loan calculatePaymentSchedule(Loan loan) throws FinanceManagerException;
}
