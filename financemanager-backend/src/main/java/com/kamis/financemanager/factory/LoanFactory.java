package com.kamis.financemanager.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.LoanPayment;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.rest.domain.loans.*;
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
		response.setCurrentPaymentNumber(loan.getCurrentPaymentNumber());
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

	/**
	 * Creates a new loan from a compare request
	 * @param request The request to create a loan from
	 * @return A new Loan object built from the request
	 */
    public static Loan buildLoanForCompareRequest(CompareLoanRequest request) {
		Loan l = new Loan();

		l.setFrequency(PaymentFrequencyEnum.valueOfLabel(request.getFrequency()));
		l.setPrincipal(request.getPrincipal());
		l.setFirstPaymentDate(request.getFirstPaymentDate());
		l.setTerm(request.getTerm());
		l.setRate(request.getRate());

		return l;
    }

	/**
	 * Builds a new CompareLoansResponse from two loans
	 * @param loan The original loan
	 * @param newLoan The new loan
	 * @return A CompareLoansResponse containing the differences between the two values
	 */
	public static CompareLoansResponse buildCompareLoansResponse(Loan loan, Loan newLoan) {
		CompareLoansResponse response = new CompareLoansResponse();
		response.setOriginalSummary(buildLoanSummary(loan));
		response.setCompareSummary(buildLoanSummary(newLoan));
		response.setNetSummary(buildNetLoanSummary(loan, newLoan));

		response.setPaymentSchedule(buildLoanPaymentScheduleComparisonList(loan, newLoan));

		return response;
	}

	/**
	 * Builds a new LoanPaymentComparisonResponse list comparing each payment item between loan and newLoan
	 * @param loan The original loan for the comparison
	 * @param newLoan The new loan for the comparison
	 * @return A list of LoanPaymentComparisonResponse objects
	 */
	private static List<LoanPaymentComparisonResponse> buildLoanPaymentScheduleComparisonList(Loan loan, Loan newLoan) {
		List<LoanPaymentComparisonResponse> responseList = new ArrayList<>();

		int maxIndex = Math.max(loan.getPayments().size(), newLoan.getPayments().size());
		int i = 0;

		while (i < maxIndex) {
			LoanPaymentComparisonResponse response = new LoanPaymentComparisonResponse();
			response.setPaymentNumber(i);

			if (i < loan.getPayments().size()) {
				response.setPaymentDate(loan.getPayments().get(i).getPaymentDate());
			} else {
				response.setPaymentDate(newLoan.getPayments().get(i).getPaymentDate());
			}

			if (i < loan.getPayments().size()) {
				response.setOriginalPayment(buildLoanPaymentComparisonItemRepsonse(loan.getPayments().get(i)));
			} else if (i > 0){
					response.setOriginalPayment(buildLoanPaymentComparisonItemResponseFromLastPay(responseList.get(i-1).getOriginalPayment()));
			} else {
				response.setOriginalPayment(new LoanPaymentComparisonItemResponse());
			}

			if (i < newLoan.getPayments().size()) {
				response.setNewPayment(buildLoanPaymentComparisonItemRepsonse(newLoan.getPayments().get(i)));
			} else if (i > 0){
				response.setNewPayment(buildLoanPaymentComparisonItemResponseFromLastPay(responseList.get(i-1).getNewPayment()));
			} else {
				response.setNewPayment(new LoanPaymentComparisonItemResponse());
			}

			response.setNetPayment(buildNetLoanPaymentComparisonItemResponse(response.getOriginalPayment(), response.getNewPayment()));
			responseList.add(response);
			i++;
		}

		return responseList;
	}

	/**
	 * Builds a LoanPaymentComparisonItemResponse from the previous response. This is done when we need to add a blank payment
	 * to keep new and original loan comparison response items 1 to 1
	 * @param prevResponse The previous loan payment to build the response from
	 * @return a LoanPaymentComparisonItemResponse built from the previous response
	 */
	private static LoanPaymentComparisonItemResponse buildLoanPaymentComparisonItemResponseFromLastPay(LoanPaymentComparisonItemResponse prevResponse) {
		LoanPaymentComparisonItemResponse response = new LoanPaymentComparisonItemResponse();

		response.setInterestToDate(prevResponse.getInterestToDate());
		response.setPrincipalToDate(prevResponse.getPrincipalToDate());

		return response;
	}

	/**
	 * Builds a Net LoanPaymentComparisonItemResponse containing the difference in values between originalPayment and newPayment
	 * @param originalPayment The original Payment's response
	 * @param newPayment The new Payment's response
	 * @return A LoanPaymentComparisonItemResponse containing the net difference between the original and new responses
	 */
	private static LoanPaymentComparisonItemResponse buildNetLoanPaymentComparisonItemResponse(LoanPaymentComparisonItemResponse originalPayment,
																							   LoanPaymentComparisonItemResponse newPayment) {
		LoanPaymentComparisonItemResponse response = new LoanPaymentComparisonItemResponse();

		response.setAmount(newPayment.getAmount() - originalPayment.getAmount());
		response.setBalance(newPayment.getBalance() - originalPayment.getBalance());
		response.setPrincipal(newPayment.getPrincipal() - originalPayment.getPrincipal());
		response.setPrincipalToDate(newPayment.getPrincipalToDate() - originalPayment.getPrincipalToDate());
		response.setInterest(newPayment.getInterest() - originalPayment.getInterest());
		response.setInterestToDate(newPayment.getInterestToDate() - originalPayment.getInterestToDate());

		return response;

	}

	/**
	 * Builds a LoanPaymentComparisonItemResponse for a loan payment
	 * @param payment The payment to build the response for
	 * @return a new LoanPaymentComparisonItemResponse for the given payment
	 */
	private static LoanPaymentComparisonItemResponse buildLoanPaymentComparisonItemRepsonse(LoanPayment payment) {
		LoanPaymentComparisonItemResponse response = new LoanPaymentComparisonItemResponse();

		response.setAmount(payment.getAmount());
		response.setBalance(payment.getBalance());
		response.setPrincipal(payment.getPrincipal());
		response.setPrincipalToDate(payment.getPrincipalToDate());
		response.setInterest(payment.getInterest());
		response.setInterestToDate(payment.getInterestToDate());

		return response;
	}

	/**
	 * Creates a net LoanSummary object for a loan which is the differences from loan compared to newLoan
	 * @param loan The original loan for the comparison
	 * @param newLoan The new loan for the comparison
	 * @return A LoanSummary populated with net differences between loan and newLoan
	 */
	private static LoanSummaryResponse buildNetLoanSummary(Loan loan, Loan newLoan) {
		LoanSummaryResponse response = new LoanSummaryResponse();
		response.setInterest(newLoan.getInterest() - loan.getInterest());
		response.setTerm(newLoan.getTerm() - loan.getTerm());
		response.setPayment(newLoan.getPayment() - loan.getPayment());

		return response;
	}

	/**
	 * Creates a LoanSummary object for a loan
	 * @param loan The loan to build the summary for
	 * @return A LoanSummary built for a given loan
	 */
	public static LoanSummaryResponse buildLoanSummary(Loan loan) {
		LoanSummaryResponse response = new LoanSummaryResponse();
		response.setInterest(loan.getInterest());
		response.setTerm(loan.getTerm());
		response.setPayment(loan.getPayment());

		return response;
	}

	/**
	 * Creates a Loan from a CalcLoanRequest
	 * @param request The request to build the loan from
	 * @return A Loan object containing details from the request
	 */
	public static Loan buildLoanForCalcLoanRequest(CalcLoanRequest request) {
		Loan l = new Loan();
		l.setRate(request.getRate());
		l.setTerm(request.getTerm());
		l.setFirstPaymentDate(request.getFirstPaymentDate());
		l.setFrequency(PaymentFrequencyEnum.valueOfLabel(request.getFrequency()));
		l.setPrincipal(request.getPrincipal());

		return l;
	}
}
