package com.kamis.financemanager.factory;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.rest.domain.loans.LoanPostRequest;
import com.kamis.financemanager.util.FinanceManagerUtil;

public class LoanFactory {

	/**
	 * Build a new Loan from a request
	 * @param request the request to build the loan from
	 * @param userId the user the new loan will belong to
	 * @return a new Loan object built from the request
	 */
	public static Loan buildLoanFromPostRequest(LoanPostRequest request, Integer userId) {
		Loan loan = new Loan();
		loan.setName(request.getName());
		loan.setUserId(userId);
		loan.setFirstPaymentDate(request.getFirstPaymentDate());
		loan.setFrequency(PaymentFrequencyEnum.valueOfLabel(request.getFrequency()));
		loan.setPrincipal(request.getPrincipal());
		loan.setRate(request.getRate());
		loan.setTerm(request.getTerm());
		loan.setAuditInfo(FinanceManagerUtil.getAuditInfo());
		
		return loan;
	}

}
