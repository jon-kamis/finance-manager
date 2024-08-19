package com.kamis.financemanager.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.LoanPayment;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.rest.domain.loans.LoanPaymentItemResponse;
import com.kamis.financemanager.rest.domain.loans.LoanRequest;
import com.kamis.financemanager.rest.domain.loans.LoanResponse;
import com.kamis.financemanager.rest.domain.loans.PagedLoanResponse;
import com.kamis.financemanager.util.FinanceManagerUtil;

public class LoanFactory {

	/**
	 * Build a new Loan from a request
	 * @param request the request to build the loan from
	 * @param userId the user the new loan will belong to
	 * @return a new Loan object built from the request
	 */
	public static Loan buildLoanFromPostRequest(LoanRequest request, Integer userId) {
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

	/**
	 * Builds a PagedLoanResponse object based on a list of loans
	 * @param loans The list of loans to build the response object for
	 * @param page The page parameter used to generate the list
	 * @param pageSize The pageSize parameter used to generate the list
	 * @param count The total number of entities returned for the query outside of paging
	 * @return A PagedLoanResponse containing response data for the list of Loans
	 */
	public static PagedLoanResponse buildPagedLoanResponse(List<Loan> loans, Integer page, Integer pageSize, int count) {
		PagedLoanResponse response = new PagedLoanResponse();
		
		response.setItems(buildLoanResponseList(loans));
		
		response.setCount(count);
		response.setPage(page != null && page > 0 ? page : 1);
		response.setPageSize(pageSize != null && pageSize > 0 ? pageSize : count);
		
		return response;
	}

	/**
	 * Builds a List of LoanResponse objects based on a list of Loans
	 * @param loans The loans to build responses for
	 * @return A list of LoanResponse objects built from the given list of Loans
	 */
	public static List<LoanResponse> buildLoanResponseList(List<Loan> loans) {
		List<LoanResponse> responseList = new ArrayList<>();
		
		for (Loan l : loans) {
			responseList.add(buildLoanResponse(l));
		}
		
		return responseList;
	}
	
	/**
	 * Builds a LoanResponse object based on a Loan object
	 * @param loan The loan to build the response for
	 * @return A LoanResponse built from the given Loan
	 */
	public static LoanResponse buildLoanResponse(Loan loan) {
		LoanResponse response = new LoanResponse();
		
		response.setId(loan.getId());
		response.setUserId(loan.getUserId());
		response.setFirstPaymentDate(loan.getFirstPaymentDate());
		response.setFrequency(loan.getFrequency().getFrequency());
		response.setBalance(loan.getBalance());
		response.setInterest(loan.getInterest());
		response.setName(loan.getName());
		response.setPayment(loan.getPayment());
		response.setPrincipal(loan.getPrincipal());
		response.setRate(loan.getRate());
		response.setTerm(loan.getTerm());
		response.setPaymentSchedule(buildPaymentScheduleResponseList(loan.getPayments()));
		
		return response;
		
	}

	/**
	 * Builds a list of LoanPaymentItemResponse objects
	 * @param payments The list of LoanPaymentItems to build the response list for
	 * @return A list of LoanPaymentResponse objects built from the given LoanPaymentItem list
	 */
	public static List<LoanPaymentItemResponse> buildPaymentScheduleResponseList(List<LoanPayment> payments) {
		List<LoanPaymentItemResponse> responseList = new ArrayList<>();
		
		if (payments == null) {
			return responseList;
		}
		
		for (LoanPayment p : payments) {
			responseList.add(buildPaymentScheduleResponse(p));
		}
		
		responseList.sort(Comparator.comparing(LoanPaymentItemResponse::getPaymentNumber));
		
		return responseList;
	}

	/**
	 * Builds a LoanPaymentItemResponse based on a LoanPayment object
	 * @param p The LoanPayment to build a response for
	 * @return A LoanPaymentItemResponse based on a LoanPayment
	 */
	public static LoanPaymentItemResponse buildPaymentScheduleResponse(LoanPayment p) {
		LoanPaymentItemResponse response = new LoanPaymentItemResponse();
		
		response.setAmount(p.getAmount());
		response.setBalance(p.getBalance());
		response.setId(p.getId());
		response.setInterest(p.getInterest());
		response.setInterestToDate(p.getInterestToDate());
		response.setPaymentDate(p.getPaymentDate());
		response.setPaymentNumber(p.getPaymentNumber());
		response.setPrincipal(p.getPrincipal());
		response.setPrincipalToDate(p.getPrincipalToDate());
		
		return response;
	}
}
