package com.kamis.financemanager.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.UserBusiness;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.factory.UserFactory;
import com.kamis.financemanager.rest.domain.users.UserResponse;

@Component
public class UserBusinessImpl implements UserBusiness {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserResponse getUserById(int id) {
		return UserFactory.buildUserResponse(userRepository.findById(id));
	}

}
