package com.kamis.financemanager.business.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.LoanBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.LoanPayment;
import com.kamis.financemanager.database.repository.LoanPaymentRepository;
import com.kamis.financemanager.database.repository.LoanRepository;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.LoanFactory;
import com.kamis.financemanager.rest.domain.loans.LoanPostRequest;
import com.kamis.financemanager.rest.domain.loans.PagedLoanResponse;
import com.kamis.financemanager.validation.LoanValidation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoanBusinessImpl implements LoanBusiness {

	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private LoanPaymentRepository loanPaymentRepository;
	
	@Autowired
	private LoanValidation loanValidation;
	
	@Lazy
	@Autowired
	private LoanBusiness loanBusiness;
	
	@Autowired
	private YAMLConfig myConfig;
	
	@Override
	public boolean createLoan(LoanPostRequest request, Integer userId) throws FinanceManagerException {
		Loan loan = LoanFactory.buildLoanFromPostRequest(request, userId);
		
		loan = loanBusiness.calculateLoanPament(loan);
		loan = loanBusiness.calculatePaymentSchedule(loan);
		loan.setBalance(getLoanBalance(loan.getPayments()));
		
		return loanRepository.saveAndFlush(loan) != null;
	}

	@Override
	public Loan calculateLoanPament(Loan loan) throws FinanceManagerException {
		
		float i; //Interest rate per payment
		float p; //Principal
		float n; //Number of total payments
		float payment; //Payment for new loan
		
		//Get payments per year
		switch(loan.getFrequency()) {
		case BIWEEKLY:
			n = 26;
			break;
		case MONTHLY:
			n = 12;
			break;
		case WEEKLY:
			n = 52;
			break;
		default:
			throw new FinanceManagerException(myConfig.getInvalidLoanPaymentFrequencyError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		// Payment Equation:
		// Payment is P / {[(1+i)^n]-1} / [i(1+i)^n] where P is starting principal, i is the interest rate divided by 12, and n is the number of payments
		i = loan.getRate() / n;
		p = loan.getPrincipal();
		n = n * (loan.getTerm() / 12);
		payment = (float) (p / ((Math.pow((i+1), n) - 1) / (i * Math.pow((i+1), n))));

		loan.setPayment(payment);
		return loan;
	}

	@Override
	public Loan calculatePaymentSchedule(Loan loan) throws FinanceManagerException {
		
		if(loan.getPayments() != null && loan.getId() != null) {
			loanPaymentRepository.deleteByLoanId(loan.getId());
			loan.setPayments(new ArrayList<>());
		}
		
		//Initialize/clear loan payments list
		loan.setPayments(new ArrayList<>());
		
		float amount = 0;
		float total = loan.getPrincipal();
		float interest = 0;
		float interestToDate = 0;
		float principal = 0;
		float principalToDate = 0;
		int paymentNum = 0;
		int paysPerYear = 0;
		
		//Convert to localdate for easier addition of days/months
		LocalDate payDate = loan.getFirstPaymentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		switch(loan.getFrequency()) {
		case MONTHLY:
			paysPerYear = 12;
			break;
		case BIWEEKLY:
			paysPerYear = 26;
			break;
		case WEEKLY:
			paysPerYear = 52;
			break;
		default:
			throw new FinanceManagerException(myConfig.getInvalidLoanPaymentFrequencyError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		while (total > 0) {
			amount = loan.getPayment();
			interest = (total * loan.getRate()) / paysPerYear;
			principal = loan.getPayment() - interest;
						
			if (total - principal > 0) {
				total -= principal;
			} else {
				principal = total - interest;
				amount = total + interest;
				total = 0;
			}
			
			interestToDate += interest;
			principalToDate += principal;

			LoanPayment payItem = new LoanPayment();
			
			payItem.setPaymentNumber(++paymentNum);
			payItem.setPrincipal(principal);
			payItem.setInterest(interest);
			payItem.setInterestToDate(interestToDate);
			payItem.setPrincipalToDate(principalToDate);
			payItem.setBalance(total);
			payItem.setAmount(amount);
			payItem.setPaymentDate(Date.from(payDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			
			//Increment paydate
			switch(loan.getFrequency()) {
			case MONTHLY:
				payDate.plusMonths(1);
				paysPerYear = 12;
				break;
			case BIWEEKLY:
				payDate.plusWeeks(2);
				paysPerYear = 26;
				break;
			case WEEKLY:
				payDate.plusWeeks(1);
				paysPerYear = 52;
				break;
			default:
				throw new FinanceManagerException(myConfig.getInvalidLoanPaymentFrequencyError(), HttpStatus.UNPROCESSABLE_ENTITY);
			}
						
			loan.AddLoanPayment(payItem);
		}

		float calcErr = loan.getPrincipal() - principalToDate;
		log.debug("Total: {}, totalPayment: {}, totalInterest: {}, Calculation Err: {}", loan.getPrincipal(), total, interestToDate, calcErr);

		//Fix calculation error
		interestToDate -= calcErr;
		loan.setInterest(interestToDate);

		return loan;
	}

	@Override
	public PagedLoanResponse getUserLoans(Integer userId, String name,
			String sortBy, String sortType, Integer page,
			Integer pageSize) {
		
		List<Loan> loans;
		int count = 0;
		boolean doCountQuery = true; //determines whether count needs queried separately
		
		//Validate request
		loanValidation.validateGetAllLoansRequest(userId, sortBy, sortType);
				
		//Create Paging and sorting
		Pageable pageable = null;
		Sort sort = null;

		//Sorting direction
		boolean sortAsc = sortType == null || sortType.isBlank() 
				|| sortType.equalsIgnoreCase(FinanceManagerConstants.SORT_TYPE_ASC);
		
		
		if (sortBy != null && !sortBy.isBlank()) {
			sort = sortAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		}
		
		if (page == null || page < 1) {
			page = 1;
		}
		
		//Pageable Info
		if (pageSize != null && pageSize >= 1 && sort != null) {
			pageable = PageRequest.of(page, pageSize, sort);
		} else if (pageSize != null && pageSize >= 1){
			pageable = PageRequest.of(page, pageSize);
		} else if (sort != null) {
			pageable = Pageable.unpaged(sort);
			doCountQuery = false;
		} else {
			pageable = Pageable.unpaged();
			doCountQuery = false;
		}
		
		//Fetch loans
		if (name != null && !name.isBlank()) {
			loans = loanRepository.findByUserIdAndName(userId, name, pageable);
			count = doCountQuery ? loanRepository.countByUserIdAndName(userId, name) : loans.size();
		} else {
			loans = loanRepository.findByUserId(userId, pageable);
			count = doCountQuery ? loanRepository.countByUserId(userId) : loans.size();
		}
		
		return LoanFactory.buildPagedLoanResponse(loans, page, pageSize, count);
	}

	@Override
	public float getLoanBalance(List<LoanPayment> payments) {
		
		if (payments == null || payments.isEmpty()) {
			return 0;
		}
		
		payments.sort(Comparator.comparing(LoanPayment::getPaymentNumber).reversed());
		
		Date today = new Date();
		
		Optional<LoanPayment> lastPay = payments.stream().filter(l -> l.getPaymentDate().before(today)).findFirst();		
		
		if (lastPay.isPresent()) {
			return lastPay.get().getBalance();
		} else {
			return 0;
		}
	}

}
