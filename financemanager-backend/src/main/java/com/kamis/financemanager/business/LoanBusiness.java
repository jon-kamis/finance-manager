package com.kamis.financemanager.business;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.loans.LoanPostRequest;
import com.kamis.financemanager.rest.domain.loans.LoanResponse;
import com.kamis.financemanager.rest.domain.loans.PagedLoanResponse;

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

	/**
	 * Retrieves all loans for a given user
	 * @param userId The user to search for
	 * @param name filter results by name
	 * @param sortBy sort by field
	 * @param sortType direction to sort
	 * @param page the page of results to return
	 * @param pageSize the pageSize of results to return
	 * @return A PagedLoanResponse of all loans matching the given criteria
	 */
	public PagedLoanResponse getUserLoans(Integer userId, String name,
			String sortBy, String sortType, Integer page,
			Integer pageSize) throws FinanceManagerException;

	/**
	 * Returns the current Loan balance based on its payments
	 * @param loan The loan to calculate the current balance for
	 * @return A float representing the current balance of a loan based on its payments.
	 */
	public float getLoanBalance(Loan loan);

	/**
	 * Returns a specific loan by its id and userId if one exists
	 * @param userId The userId of the loan to fetch
	 * @param loanId The id of the loan to fetch
	 * @return A Loan object matching the given criteria or null if one is not found
	 */
	public LoanResponse getLoanById(Integer userId, Integer loanId);
}
