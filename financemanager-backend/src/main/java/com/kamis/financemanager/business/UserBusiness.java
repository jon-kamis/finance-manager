package com.kamis.financemanager.business;

import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.users.UserResponse;

public interface UserBusiness {
	
	public UserResponse getUserById(int id) throws FinanceManagerException;
}
