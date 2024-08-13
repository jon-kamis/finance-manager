package com.kamis.financemanager.business;

import java.util.Date;

import com.kamis.financemanager.database.domain.Income;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.incomes.IncomeExpirationRequest;
import com.kamis.financemanager.rest.domain.incomes.IncomePostRequest;
import com.kamis.financemanager.rest.domain.incomes.IncomeResponse;
import com.kamis.financemanager.rest.domain.incomes.IncomeSummaryResponse;
import com.kamis.financemanager.rest.domain.incomes.PagedIncomeResponse;

public interface IncomeBusiness {

	/**
	 * CreateIncome generates a new income for a user
	 * 
	 * @param userId  The user to create the income for
	 * @param request The request for the new income
	 * @return true if the income was created and saved successfully
	 * @throws FinanceManagerException
	 */
	public boolean createIncome(Integer userId, IncomePostRequest request) throws FinanceManagerException;

	/**
	 * Attempts to find an income by its id and return the response to the user
	 * 
	 * @param userId The userId to search for
	 * @param id     The id of the income to search for
	 * @return An IncomeResponse if one is found or null if not
	 * @throws FinanceManagerException
	 */
	public IncomeResponse findByUserIdAndId(Integer userId, Integer id) throws FinanceManagerException;

	/**
	 * Attempts to calculate simple taxes for an income
	 * 
	 * @param income The income to calculate
	 * @return The federal tax amount on this income
	 * @throws FinanceManagerException
	 */
	public float calculateStandardFederalTaxes(Income income) throws FinanceManagerException;

	/**
	 * Attempts to calculate Social Security Tax for an income
	 * 
	 * @param income The income to calculate social security for
	 * @return The social security tax amount on this income
	 * @throws FinanceManagerException
	 */
	public float calculateSocialSecurityTax(Income income);

	/**
	 * Attempts to calculate Medicare Tax for an income
	 * 
	 * @param income The income to calculate medicare for
	 * @return The medicare tax amount on this income
	 * @throws FinanceManagerException
	 */
	public float calculateMedicareTax(Income income);

	/**
	 * Retrieves all user incomes and a summary of the incomes
	 * 
	 * @param userId   The userId to search for
	 * @param name     The name to filter by
	 * @param sortBy   Sorting Options
	 * @param sortType Sorting direction
	 * @param page     The page of results to return
	 * @param pageSize The size of pages to return
	 * @return A PagedIncomeResponse containing all user incomes and summary data
	 * @throws FinanceManagerException
	 */
	public PagedIncomeResponse getUserIncomes(Integer userId, String name, String sortBy, String sortType, Integer page,
			Integer pageSize) throws FinanceManagerException;

	/**
	 * Returns an income summary of a user's income
	 * @param userId The user to build the summary for
	 * @param date A date containing the year and month desired for the report. Null dates result in using the current date
	 * @return A summary of a user's income for a year
	 * @throws FinanceManagerException
	 */
	public IncomeSummaryResponse getUserIncomeSummary(Integer userId, Date date) throws FinanceManagerException;

	/**
	 * Attempts to expire an income by its id. Also expires linked transactions
	 * @param userId the id of the user who owns this income. used to verify ownership
	 * @param id The id of the income to expire
	 * @param request The request containing the new expiration date
	 * @return true if the income was expired successfully
	 * @throws FinanceManagerException
	 */
	public boolean expireIncomeById(Integer userId, Integer id, IncomeExpirationRequest request) throws FinanceManagerException;

	/**
	 * Attempts to delete an income by its id. Also deletes linked transactions
	 * @param userId The id of the user who owns this income. used to verify ownership
	 * @param id The id of the income to delete
	 * @return true if the income was deleted successfully
	 * @throws FinanceManagerException
	 */
	public boolean deleteIncomeById(Integer userId, Integer id) throws FinanceManagerException;
}
