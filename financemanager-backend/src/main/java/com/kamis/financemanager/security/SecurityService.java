package com.kamis.financemanager.security;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.exception.FinanceManagerException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SecurityService {

	private UserRepository userRepository;
	
	private YAMLConfig myConfig;
	
	public SecurityService(UserRepository userRepository, YAMLConfig myConfig) {
		this.userRepository = userRepository;
		this.myConfig = myConfig;
	}
	
	public boolean hasAccess(Authentication authentication, Integer userId) {
		
		if (authentication.getAuthorities().stream()
				.filter(a -> a.getAuthority()
						.equalsIgnoreCase(FinanceManagerConstants.ADMIN_ROLE))
				.findFirst().isPresent()) {
			log.debug("bypassing security check for administrator");
			return true;
		}
		
		if (userId == null || userId < 1) {
			throw new FinanceManagerException(myConfig.getUserIdRequiredError(), HttpStatus.BAD_REQUEST);
		}
		
		log.info("Inside securityservice.hasAccess");
		Optional<User> u = userRepository.findById(userId);
		
		return u.isPresent() && u.get().getUsername().equals(authentication.getName());
	}

}
