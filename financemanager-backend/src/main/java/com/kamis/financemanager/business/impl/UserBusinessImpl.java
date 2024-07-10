package com.kamis.financemanager.business.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.UserBusiness;
import com.kamis.financemanager.constants.ErrorConstants;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.UserFactory;
import com.kamis.financemanager.rest.domain.users.UserResponse;

@Component
public class UserBusinessImpl implements UserBusiness {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserResponse getUserById(int id) {
		Optional<User> optUser = userRepository.findById(id);

		//Throw exception if user was not found
		if (!optUser.isPresent()) {
			throw new FinanceManagerException(ErrorConstants.genericNotFoundErrorMsg, HttpStatus.NOT_FOUND);
		}
		
		return UserFactory.buildUserResponse(optUser.get());
	}

}
