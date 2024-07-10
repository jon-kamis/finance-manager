package com.kamis.financemanager.business;

import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.users.UserResponse;

public interface UserBusiness {
	
	/**
	 * Attempts to retrieve a user response for a user by its id
	 * @param id the id of the user to search for
	 * @return A UserResponse containing information on the user
	 * @throws FinanceManagerException
	 */
	public UserResponse getUserById(int id) throws FinanceManagerException;
}
