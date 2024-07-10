package com.kamis.financemanager.security.jwt;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.constants.LoggingConstants;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.rest.domain.auth.AuthRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginService{

	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * Validates if credentials in an AuthRequest are valid
	 * @param request The request to validate
	 * @return true if the credentials are valid and false otherwise
	 */
	public boolean Login(AuthRequest request) {
		final String method = "LoginService.Login";
		log.debug(LoggingConstants.ENTER_LOG, method);
		
		if (request.getUsername().isBlank() || request.getPassword().isBlank()) {
			log.debug("{} recieved request with empty username or password", method);
			log.debug(LoggingConstants.EXIT_LOG, method);
			return false;
		}
		
		Optional<User> optUser = userRepository.findByUsername(request.getUsername());
		
		if (optUser.isEmpty()) {
			log.debug("{} recieved request with empty username or password", method);
			log.debug(LoggingConstants.EXIT_LOG, method);
			return false;
		}
		
		User u = optUser.get();
		if (u.getUsername().equals(request.getUsername())) {	
			if (encoder.matches(request.getPassword(), u.getPassword())) {
				log.debug("{} login successful", method);
				log.debug(LoggingConstants.EXIT_LOG, method);
				return true;
			}
		}
		
		log.debug(LoggingConstants.EXIT_LOG, method);
		return false;
		
	}
}
