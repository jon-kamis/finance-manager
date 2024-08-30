package com.kamis.financemanager.business;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.loans.*;

public interface LoanBusiness {

	/**
	 * Attempts to create a new Loan for a user
	 * @param request A request containing the loan information
	 * @param userId The id of the user to create the loan for
	 * @return true if the loan was created successfully
	 * @throws FinanceManagerException
	 */
	boolean createLoan(LoanRequest request, Integer userId) throws FinanceManagerException;

	/**
	 * Attempts to calculate all values for a Loan
	 * @param l The loan to calculate values for
	 * @return l with all calculation values populated
	 */
	Loan calculateLoanValues(Loan l);

	/**
	 * Attempts to update a Loan's details with the latest paymentNumber and balance
	 * @param l The loan to update
	 * @return l with its values updated
	 */
	Loan updateLatestDetails(Loan l);

	/**
	 * Attempts to perform payment calculations on a new Loan object
	 * @param loan The loan to perform the payment calculations on
	 * @return The loan with payment fields populated
	 * @throws FinanceManagerException
	 */
	Loan calculateLoanPayment(Loan loan) throws FinanceManagerException;
	
	/**
	 * Calculates the payment schedule for a loan
	 *
	 * @param loan The loan to perform the payment schedule calculation on
	 * @return The loan with payment schedule list filled
	 * @throws FinanceManagerException
	 */
	Loan calculatePaymentSchedule(Loan loan) throws FinanceManagerException;

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
	PagedLoanResponse getUserLoans(Integer userId, String name,
			String sortBy, String sortType, Integer page,
			Integer pageSize) throws FinanceManagerException;

	/**
	 * Returns the current Loan balance based on its payments
	 * @param loan The loan to calculate the current balance for
	 * @return A float representing the current balance of a loan based on its payments.
	 */
	float getLoanBalance(Loan loan);

	/**
	 * Returns the current Loan Payment number
	 * @param loan The loan to get the payment number for
	 * @return an int representing the number of the last payment from the payment schedule
	 */
	int getCurrentLoanPaymentNumber(Loan loan);

	/**
	 * Returns a specific loan by its id and userId if one exists
	 * @param userId The userId of the loan to fetch
	 * @param loanId The id of the loan to fetch
	 * @return A Loan object matching the given criteria or null if one is not found
	 */
	LoanResponse getLoanById(Integer userId, Integer loanId);

	/**
	 * Updates balances for all loans
	 */
	void updateLoanBalances();

	/**
	 * Updates balances for all loans in the background
	 */
	void updateLoanBalancesAsync();

	/**
	 * Attempts to delete a loan for a given user by its id
	 * @param userId The userId of the user owning the loan
	 * @param loanId The id of the loan to delete
	 * @return true if the loan is deleted
	 * @throws FinanceManagerException
	 */
	boolean deleteLoanById(Integer userId, Integer loanId) throws FinanceManagerException;

	/**
	 * Attempts to get a loan summary for a user
	 * @param userId The id of the user to get the summary for
	 * @return A UserLoanSummary for the given user
	 */
	UserLoanSummaryResponse getUserLoanSummary(Integer userId);

	/**
	 * Attempts to update a loan by its id for a given user
	 * @param userId The userId of the owner of the loan to update
	 * @param loanId The id of the loan to update
	 * @param request The updated values for the loan
	 * @return A LoanResponse containing the updated loan values
	 * @throws FinanceManagerException
	 */
	LoanResponse updateLoanById(Integer userId, Integer loanId, LoanRequest request) throws FinanceManagerException;

	/**
	 * Compares two loans generated from requests
	 * @param request The request containing the new loan details to compare with
	 * @return A CompareLoansResponse containing all the differences between the existing loan and the new one
	 */
    CompareLoansResponse compareLoans(CompareLoansRequest request);

	/**
	 * Calculates payment details for a loan and returns them without saving anything
	 * @param request A CalcLoanRequest containing details for the loan to calculate
	 * @return a LoanResponse containing all of the loan and loan payment details for the given request
	 */
    LoanResponse calculateLoanValues(CalcLoanRequest request);

	/**
	 * Attempts to create a new Manual Loan Payment for a Loan and save it before recalculating the loan
	 * @param loanId The loanId of the loan to update
	 * @param request A request containing information for the new manual payment
	 * @return true if the loan was created successfully
	 * @throws FinanceManagerException
	 */
    boolean addLoanPayment(Integer loanId, ManualLoanPaymentRequest request) throws FinanceManagerException;

	/**
	 * Syncs Loan transactions by deleting all existing transactions for the given user's loans and regenerating them all
	 * @param userId The userId of the user to sync transactions for
	 */
	void syncLoanTransactions(Integer userId);

	/**
	 * Attempts to delete a loan manual payment by its id
	 * @param loanId The id of the loan the payment belongs to
	 * @param paymentId The id of the payment to delete
	 * @return true if the loanPayment is deleted successfully
	 * @throws FinanceManagerException
	 */
	boolean deleteLoanPayment(Integer loanId, Integer paymentId) throws FinanceManagerException;
}
