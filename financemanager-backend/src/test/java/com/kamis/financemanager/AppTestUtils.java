package com.kamis.financemanager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.kamis.financemanager.database.domain.*;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.StateEnum;
import com.kamis.financemanager.rest.domain.loans.LoanRequest;
import com.kamis.financemanager.rest.domain.loans.ManualLoanPaymentRequest;

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
		user.setState(StateEnum.PENNSYLVANIA);
		user.setLocalTaxRate((float)0.0005);
		user.setEmail(name + "@fm.com");
		user.setPassword(name + "password");
		user.setAuditInfo(getTestAuditInfo());
		user.addRole(role, TestUserName);
		
		return user;
	}

	/**
	 * Builds a populated User object for testing
	 * @param name The username of the user
	 * @return A populated User object
	 */
	public static User buildUserNoRole(String name) {
		User user = new User();
		user.setFirstName(name);
		user.setLastName(name);
		user.setUsername(name);
		user.setState(StateEnum.PENNSYLVANIA);
		user.setLocalTaxRate((float)0.0005);
		user.setEmail(name + "@fm.com");
		user.setPassword(name + "password");
		user.setAuditInfo(getTestAuditInfo());

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
		loan.setRate((float)0.045);		
		loan.setPrincipal(principal);
		loan.setFirstPaymentDate(Date.from(startDt.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		loan.setFrequency(PaymentFrequencyEnum.BIWEEKLY);
		loan.setTerm(60);
		
		loan.setUserId(user.getId());
		loan.setAuditInfo(getTestAuditInfo());
		
		return loan;
	}

	/**
	 * Builds a LoanManualPayment for testing
	 * @param amount The amount to give the loan
	 * @param startDt The startDt for the payment
	 * @param endDate The endDt for the payment
	 * @return A LoanManualPayment
	 */
	public static LoanManualPayment buildLoanManualPayment(float amount, LocalDate startDt, LocalDate endDate) {
		LoanManualPayment manualPayment = new LoanManualPayment();
		manualPayment.setAmount(amount);
		manualPayment.setEffectiveDate(Date.from(startDt.atStartOfDay(ZoneId.systemDefault()).toInstant()));

		if (endDate != null) {
			manualPayment.setExpirationDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}

		return manualPayment;
	}

	/**
	 * Builds a ManualLoanPaymentRequest for testing
	 * @param amount The amount to make the request
	 * @param startDt The startDate for the new payment
	 * @param endDate The endDate for the new payment
	 * @return A ManualLoanPaymentRequest
	 */
	public static ManualLoanPaymentRequest buildManualLoanPaymentRequest(float amount, LocalDate startDt, LocalDate endDate) {
		ManualLoanPaymentRequest request = new ManualLoanPaymentRequest();
		request.setAmount(amount);
		request.setEffectiveDate(Date.from(startDt.atStartOfDay(ZoneId.systemDefault()).toInstant()));

		if (endDate != null) {
			request.setExpirationDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}

		return request;
	}

	/**
	 * Mocks a LoanRequest with the given values
	 * @param name The loan name
	 * @param term the Loan's term
	 * @param rate The Loan's rate
	 * @param frequency The loan's frequency
	 * @param principal the Loan's principal
	 * @param startDate the Loan's firstPaymentDate
	 * @return A LoanRequest with the given information
	 */
	public static LoanRequest mockLoanRequest(String name, Integer term, Float rate, String frequency, Float principal, LocalDate startDate) {
		LoanRequest request = new LoanRequest();
		request.setName(name);
		request.setTerm(term);
		request.setRate(rate);
		request.setFrequency(frequency);
		request.setPrincipal(principal);

		if (startDate != null) {
			request.setFirstPaymentDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}

		return request;
	}
}
