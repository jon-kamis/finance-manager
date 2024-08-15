package com.kamis.financemanager.business;

import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.auth.RegistrationRequest;
import com.kamis.financemanager.rest.domain.users.UserMonthlySummaryResponse;
import com.kamis.financemanager.rest.domain.users.UserResponse;

public interface UserBusiness {
	
	/**
	 * Attempts to retrieve a user response for a user by its id
	 * @param id the id of the user to search for
	 * @return A UserResponse containing information on the user
	 * @throws FinanceManagerException
	 */
	public UserResponse getUserById(int id) throws FinanceManagerException;
	
	/**
	 * Attempts to register a new user into the application
	 * @return true if the request was successful
	 * @throws FinanceManagerException
	 */
	public boolean registerUser(RegistrationRequest request) throws FinanceManagerException;

	/**
	 * Attempts to retrieve a user's month summary for a given year and month
	 * @param id the Id of the user to retrieve
	 * @param year The year of the summary to build
	 * @param month The month of the summary to build
	 * @return A UserMonthlySummaryResponse containing the given user's financial summary for the given year and month
	 * @throws FinanceManagerException
	 */
    public UserMonthlySummaryResponse getUserMonthlySummary(int id, String year, String month) throws FinanceManagerException;
}
