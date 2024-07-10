package com.kamis.financemanager.factory;

import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.users.UserResponse;

public class UserFactory {

	/**
	 * Builds a UserResponse object from a User object
	 * @param u The user object to convert User object
	 * @return A UserResponse containing data from the user object
	 * @throws FinanceManagerException
	 */
	public static UserResponse buildUserResponse(User u) throws FinanceManagerException {
		
		
		UserResponse resp = new UserResponse();
		resp.setDisplayName(u.getLastName() + ", " + u.getFirstName());
		resp.setFirstName(u.getFirstName());
		resp.setUsername(u.getUsername());
		resp.setEmail(u.getEmail());
		resp.setId(u.getId());
		
		return resp;
	}
	
}
