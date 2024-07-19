package com.kamis.financemanager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.kamis.financemanager.database.domain.AuditInfo;
import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.Role;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;

public class AppTestUtils {
	
	public static final String TestUserName = "finacemanager-test";
	
	/**
	 * Builds a populated AuditInfo object for testing
	 * @return A populated AuditInfo object
	 */
	public static AuditInfo getTestAuditInfo() {
		AuditInfo auditInfo = new AuditInfo();
		
		auditInfo.setCreateDt(new Date());
		auditInfo.setLastUpdateBy(TestUserName);
		auditInfo.setLastUpdateDt(new Date());
		
		return auditInfo;
	}

	/**
	 * Builds a populated User object for testing
	 * @param name The username of the user
	 * @param role A role to give the user
	 * @return A populated User object
	 */
	public static User buildUser(String name, Role role) {
		User user = new User();
		user.setFirstName(name);
		user.setLastName(name);
		user.setUsername(name);
		user.setEmail(name + "@fm.com");
		user.setPassword(name + "password");
		user.setAuditInfo(getTestAuditInfo());
		user.addRole(role, TestUserName);
		
		return user;
	}

	/**
	 * Builds a populated Role object for testing
	 * @param name The name of the role
	 * @return A populated Role object
	 */
	public static Role buildRole(String name) {
		Role role = new Role();
		role.setName(name);
		role.setAuditInfo(getTestAuditInfo());
		return role;
	}

	
	public static Loan buildLoan(User user, String name, float principal, float balance, LocalDate startDt) {
		Loan loan = new Loan();
		
		loan.setBalance(balance);
		loan.setName(name);
		loan.setPrincipal(principal);
		loan.setFirstPaymentDate(Date.from(startDt.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		loan.setFrequency(PaymentFrequencyEnum.BIWEEKLY);
		loan.setTerm(60);
		
		loan.setUserId(user.getId());
		loan.setAuditInfo(getTestAuditInfo());
		
		return loan;
	}

}
