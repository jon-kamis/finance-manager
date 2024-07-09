package com.kamis.financemanager.factory;

import java.util.Optional;

import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.users.UserResponse;

public class UserFactory {

	public static UserResponse buildUserResponse(Optional<User> optional) throws FinanceManagerException {
		
		if (!optional.isPresent()) {
			throw new FinanceManagerException();
		}
		
		User u = optional.get();
		
		UserResponse resp = new UserResponse();
		resp.setDisplayName(u.getLastName() + ", " + u.getFirstName());
		resp.setFirstName(u.getFirstName());
		resp.setUsername(u.getUsername());
		resp.setEmail(u.getEmail());
		resp.setId(u.getId());
		
		return resp;
	}
	
}
