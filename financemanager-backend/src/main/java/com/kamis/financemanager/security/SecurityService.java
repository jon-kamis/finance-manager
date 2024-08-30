package com.kamis.financemanager.security;

import java.util.Objects;
import java.util.Optional;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

	private final UserRepository userRepository;
	
	private final YAMLConfig myConfig;

	private final LoanRepository loanRepository;
	
	public SecurityService(UserRepository userRepository, LoanRepository loanRepository, YAMLConfig myConfig) {
		this.userRepository = userRepository;
		this.myConfig = myConfig;
		this.loanRepository = loanRepository;
	}
	
	public boolean isOwner(Authentication authentication, Integer userId) {
		log.debug("Inside securityservice.isOwner");
		
		if (userId == null || userId < 1) {
			log.debug("userId is invalid");
			throw new FinanceManagerException(myConfig.getUserIdRequiredError(), HttpStatus.BAD_REQUEST);
		}
		
		Optional<User> u = userRepository.findById(userId);
		
		return u.isPresent() && u.get().getUsername().equals(authentication.getName());
	}
	
	public boolean isAdmin(Authentication authentication) {
		log.debug("Inside securityservice.isAdmin");
		if (authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority()
						.equalsIgnoreCase(FinanceManagerConstants.ADMIN_ROLE))) {
			log.debug("user is administrator");
			return true;
		}
		
		log.debug("user is not administrator");
		return false;
	}
	
	/**
	 * Determines if the User has access to a resource by the userId path variable
	 * @param authentication Spring security Authentication object containing the logged in user's details
	 * @param userId The Id of the user the requested resource belongs to
	 * @return true if the user is an administrator or owns the requested resource
	 */
	public boolean hasAccess(Authentication authentication, Integer userId) {
		
		return isAdmin(authentication) || isOwner(authentication, userId);
		
	}

	/**
	 * Determines if the User has access to a loan by the loan's id and the path variable
	 * @param authentication Spring security Authentication object containing the logged in user's details
	 * @param loanId The id of the loan the requested resource belongs to
	 * @return true if the user is an administrator or owns the requested resource
	 */
	public boolean hasAccessToLoan(Authentication authentication, Integer loanId) {
		if (isAdmin(authentication)) {
			return true;
		}

		Optional<Loan> loan = loanRepository.findById(loanId);
		Optional<User> user = userRepository.findByUsername(authentication.getName());

        return loan.isPresent() && user.isPresent() && Objects.equals(loan.get().getUserId(), user.get().getId());
	}

}
