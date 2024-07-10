package com.kamis.financemanager.business.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.UserBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.RoleRepository;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.UserFactory;
import com.kamis.financemanager.rest.domain.auth.RegistrationRequest;
import com.kamis.financemanager.rest.domain.users.UserResponse;

@Component
public class UserBusinessImpl implements UserBusiness {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private YAMLConfig myConfig;

	@Override
	public UserResponse getUserById(int id) {
		Optional<User> optUser = userRepository.findById(id);

		//Throw exception if user was not found
		if (!optUser.isPresent()) {
			throw new FinanceManagerException(myConfig.getGenericNotFoundErrorMsg(), HttpStatus.NOT_FOUND);
		}
		
		return UserFactory.buildUserResponse(optUser.get());
	}

	@Override
	public boolean registerUser(RegistrationRequest request) throws FinanceManagerException {
		
		//Validate that all fields are populated
		if (request.getUsername().isBlank() || request.getPassword().isBlank() || request.getEmail().isBlank()
				|| request.getFirstName().isBlank() || request.getLastName().isBlank()) {
			throw new FinanceManagerException(myConfig.getRequiredFieldsBlankError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		//Validate Username and email are available
		boolean usernameExists = userRepository.countByUsername(request.getUsername()) > 0;
		boolean emailExists = userRepository.countByUsername(request.getUsername()) > 0;

		if (usernameExists) {
			throw new FinanceManagerException(myConfig.getUsernameExistsError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		if (emailExists) {
			throw new FinanceManagerException(myConfig.getEmailExistsError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		//Validate Password contains at least 1 letter
		if(!request.getPassword().matches(".*[a-zA-Z]+.*")) {
			throw new FinanceManagerException(myConfig.getInvalidPasswordError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		//Encode password
		request.setPassword(encoder.encode(request.getPassword()));
		
		//Build User
		User user = UserFactory.buildUserForRegistration(request);
		
		//Fetch default user role
		Role role = roleRepository.findByName(myConfig.getDefaultUserRole());
		
		//Add default user role
		user.addRole(role, myConfig.getApplicationUsername());
		
		return userRepository.saveAndFlush(user) != null;
	}

}
