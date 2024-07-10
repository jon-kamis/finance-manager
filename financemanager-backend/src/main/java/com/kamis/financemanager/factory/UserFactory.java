package com.kamis.financemanager.factory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.kamis.financemanager.constants.LoggingConstants;
import com.kamis.financemanager.database.domain.AuditInfo;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.domain.UserRole;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.auth.RegistrationRequest;
import com.kamis.financemanager.rest.domain.users.UserResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserFactory {

	/**
	 * Builds a UserResponse object from a User object
	 * @param u The user object to convert User object
	 * @return A UserResponse containing data from the user object
	 * @throws FinanceManagerException
	 */
	public static UserResponse buildUserResponse(User u) throws FinanceManagerException {
		String method = "UserFactory.buildUserResponse";
		log.debug(LoggingConstants.ENTER_LOG, method);
		
		UserResponse resp = new UserResponse();
		resp.setDisplayName(u.getLastName() + ", " + u.getFirstName());
		resp.setFirstName(u.getFirstName());
		resp.setLastName(u.getLastName());
		resp.setUsername(u.getUsername());
		resp.setEmail(u.getEmail());
		resp.setId(u.getId());
		
		log.debug(LoggingConstants.EXIT_LOG, method);
		return resp;
	}

	/**
	 * Builds a User objecet from a registration request
	 * @param request The request to build the user for
	 * @return A built user object for a registration request
	 */
	public static User buildUserForRegistration(RegistrationRequest request) {
		String method = "UserFactory.buildUserForRegistration";
		log.debug(LoggingConstants.ENTER_LOG, method);
		
		User u = new User();
		u.setFirstName(request.getFirstName());
		u.setLastName(request.getLastName());
		u.setEmail(request.getEmail());
		u.setUsername(request.getUsername());
		u.setPassword(request.getPassword());
		
		AuditInfo auditInfo = new AuditInfo();
		auditInfo.setCreateDt(new Date(System.currentTimeMillis()));
		auditInfo.setLastUpdateDt(new Date(System.currentTimeMillis()));
		auditInfo.setLastUpdateBy(request.getUsername());
		
		u.setAuditInfo(auditInfo);
		
		log.debug(LoggingConstants.EXIT_LOG, method);
		return u;
	}
	
}
