package com.kamis.financemanager.business;

import com.kamis.financemanager.database.domain.Income;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.incomes.IncomePostRequest;
import com.kamis.financemanager.rest.domain.incomes.IncomeResponse;

public interface IncomeBusiness {

	/**
	 * CreateIncome generates a new income for a user
	 * @param userId The user to create the income for
	 * @param request The request for the new income
	 * @return true if the income was created and saved successfully
	 * @throws FinanceManagerException
	 */
	public boolean createIncome(Integer userId, IncomePostRequest request) throws FinanceManagerException;

	/**
	 * Attempts to find an income by its id and return the response to the user
	 * @param userId The userId to search for
	 * @param id The id of the income to search for
	 * @return An IncomeResponse if one is found or null if not
	 * @throws FinanceManagerException
	 */
	public IncomeResponse findByUserIdAndId(Integer userId, Integer id) throws FinanceManagerException;

	/**
	 * Attempts to calculate simple taxes for an income
	 * @param income The income to calculate
	 * @return The federal tax amount on this income
	 * @throws FinanceManagerException
	 */
	public float calculateStandardFederalTaxes(Income income) throws FinanceManagerException;

	/**
	 * Attempts to calculate Social Security Tax for an income
	 * @param income The income to calculate social security for
	 * @return The social security tax amount on this income
	 * @throws FinanceManagerException
	 */
	public float calculateSocialSecurityTax(Income income);
	
	/**
	 * Attempts to calculate Medicare Tax for an income
	 * @param income The income to calculate medicare for
	 * @return The medicare tax amount on this income
	 * @throws FinanceManagerException
	 */
	public float calculateMedicareTax(Income income);

}
