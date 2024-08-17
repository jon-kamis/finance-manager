package com.kamis.financemanager.business;

import java.util.Date;
import java.util.List;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.LoanPayment;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.transactions.PagedTransactionOccurrenceResponse;
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
	 * @param parentName Filters results by parent table name
	 * @param category Filter results by category
	 * @param type Filter results by type
	 * @param sortBy Sort results
	 * @param sortType Direction of sorting
	 * @param page Page of results to return
	 * @param pageSize PageSize of results to return
	 * @return A PagedTransactionReponse containing all user transactions meeting the given criteria
	 */
	public PagedTransactionResponse getAllUserTransactions(Integer userId, String name, String parentName, String category, String type,
			String sortBy, String sortType, Integer page, Integer pageSize) throws FinanceManagerException;

	/**
	 * Returns a list of all pay dates in a date range
	 * @param t The transaction to find pays for
	 * @param startDate The starting date of the range
	 * @param endDate The ending date of the range
	 * @return A list of pay dates for a given transaction inside of a given date range
	 * @throws FinanceManagerException
	 */
	public List<Date> getPaysInDateRange(Transaction t, Date startDate, Date endDate) throws FinanceManagerException;

	/**
	 * Returns a list of all User Transaction Occurrences for a date range
	 * @param userId The id of the user to retrieve transaction occurrences for
	 * @param startDate The starting date of the range to search
	 * @param endDate The ending date of the range to search
	 * @param name Filter results by name
	 * @param parent Filter results by parent type
	 * @param category Filter results by category
	 * @param type Filter results by transaction type
	 * @param sortBy Sort Results by field
	 * @param sortType Sort Direction
	 * @param page Page of results to return
	 * @param pageSize Size of pages to return
	 * @return A PagedTransactionOccurrenceResponse holding the requested data
	 */
	public PagedTransactionOccurrenceResponse getAllUserTransactionOccurrences(Integer userId, Date startDate, Date endDate, String name, String parent, String category, String type, String sortBy, String sortType, Integer page, Integer pageSize);

	/**
	 * Fetches all transactions for a given user and date range
	 * @param userId The id of the user to fetch transactions for
	 * @param startDt The starting Date of the range to fetch transactions for
	 * @param endDt The ending Date of the range to fetch transactions for
	 * @param name Filter results by name
	 * @param parent Filter results by parent type
	 * @param category Filter results by category
	 * @param type Filter results by transaction type
	 * @param sortBy Sort Results by field
	 * @param sortType Sort Direction
	 * @param page Page of results to return
	 * @param pageSize Size of pages to return
	 * @return A list of all transactions for the given user which are effective for at least one day of the given range
	 */
	public List<Transaction> getTransactionsForDateRange(int userId, Date startDt, Date endDt, String name, String parent, String category, String type, String sortBy, String sortType, Integer page, Integer pageSize);

	/**
	 * Generates and saves a new list of Transactions for a loan payment list
	 * @param loanPayments The list of loan payments to save the transactions for
	 */
	public void buildAndSaveTransactionsForLoanPayments(List<LoanPayment> loanPayments);

	/**
	 * Deletes all Transactions by a Loan
	 * @param l The loan to delete transactions for
	 */
	public void deleteByLoan(Loan l);
}
