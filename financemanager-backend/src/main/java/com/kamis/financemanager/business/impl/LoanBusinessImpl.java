package com.kamis.financemanager.business.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import com.kamis.financemanager.business.TransactionBusiness;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.rest.domain.loans.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.LoanBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.LoanPayment;
import com.kamis.financemanager.database.repository.LoanPaymentRepository;
import com.kamis.financemanager.database.repository.LoanRepository;
import com.kamis.financemanager.database.specifications.GenericSpecification;
import com.kamis.financemanager.database.specifications.QueryOperation;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.LoanFactory;
import com.kamis.financemanager.util.FinanceManagerUtil;
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
	private UserRepository userRepository;

	@Autowired
	private LoanValidation loanValidation;
	
	@Lazy
	@Autowired
	private LoanBusiness loanBusiness;

	@Autowired
	private TransactionBusiness transactionBusiness;

	@Autowired
	private YAMLConfig myConfig;
	
	@Override
	public boolean createLoan(LoanRequest request, Integer userId) throws FinanceManagerException {
		Loan loan = LoanFactory.buildLoanFromPostRequest(request, userId);
		
		loan = loanBusiness.calculateLoanPayment(loan);
		loan = loanBusiness.calculatePaymentSchedule(loan);
		loan.setBalance(getLoanBalance(loan));
		loan.setCurrentPaymentNumber(getCurrentLoanPaymentNumber(loan));

        loanRepository.saveAndFlush(loan);
		transactionBusiness.buildAndSaveTransactionsForLoanPayments(loan.getPayments(), userId);
        return true;
	}

	@Override
	public Loan calculateLoanPayment(Loan loan) throws FinanceManagerException {
		
		float i; //Interest rate per payment
		float p; //Principal
		float n; //Number of total payments
		float payment; //Payment for new loan
		
		//Get payments per year
        n = switch (loan.getFrequency()) {
            case BIWEEKLY -> 26;
            case MONTHLY -> 12;
            case WEEKLY -> 52;
            default ->
                    throw new FinanceManagerException(myConfig.getInvalidLoanPaymentFrequencyError(), HttpStatus.UNPROCESSABLE_ENTITY);
        };
		
		// Payment Equation:
		// Payment is P / {[(1+i)^n]-1} / [i(1+i)^n] where P is starting principal, i is the interest rate divided by 12, and n is the number of payments
		i = loan.getRate() / n;
		p = loan.getPrincipal();
		n = n * ((float) loan.getTerm() / 12);
		payment = (float) (p / ((Math.pow((i+1), n) - 1) / (i * Math.pow((i+1), n))));

		loan.setPayment(((float)Math.round(payment * 100)) / 100);
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
		
		//Convert to local date for easier addition of days/months
		LocalDate payDate = loan.getFirstPaymentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        paysPerYear = switch (loan.getFrequency()) {
            case MONTHLY -> 12;
            case BIWEEKLY -> 26;
            case WEEKLY -> 52;
            default ->
                    throw new FinanceManagerException(myConfig.getInvalidLoanPaymentFrequencyError(), HttpStatus.UNPROCESSABLE_ENTITY);
        };
		
		while (total > 0) {
			amount = loan.getPayment();
			interest = ((float)Math.round(((total * loan.getRate()) / paysPerYear) * 100)) / 100;
			principal = loan.getPayment() - interest;
						
			if (total - principal > 0) {
				total -= principal;
			} else {
				principal = total;
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
			
			//Increment pay date
             switch (loan.getFrequency()) {
                case MONTHLY -> {
                    payDate = payDate.plusMonths(1);
                }
                case BIWEEKLY -> {
                    payDate = payDate.plusWeeks(2);
                }
                case WEEKLY -> {
                    payDate = payDate.plusWeeks(1);
                }
                default ->
                        throw new FinanceManagerException(myConfig.getInvalidLoanPaymentFrequencyError(), HttpStatus.UNPROCESSABLE_ENTITY);
            };
						
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
		
		//Build specification
		GenericSpecification<Loan> spec = new GenericSpecification<>();
		spec = spec.where("userId", userId, QueryOperation.EQUALS);
		
		if (name != null && !name.isBlank()) {
			spec = spec.and("name", name, QueryOperation.CONTAINS);
		}
		
		//Validate request
		loanValidation.validateGetAllLoansRequest(userId, sortBy, sortType);
				
		//Create Paging and sorting
		Sort sort = FinanceManagerUtil.buildSort(sortBy, sortType);
		Pageable pageable = FinanceManagerUtil.buildPageable(page, pageSize, sort);

		//Fetch loans
		if (pageable != null) {
			loans = loanRepository.findAll(spec.build(), pageable).toList();
			count = (int)loanRepository.count(spec.build());
		} else if (sort != null) {
			loans = loanRepository.findAll(spec.build(), sort);
			count = loans.size();
		} else {
			loans = loanRepository.findAll(spec.build());
			count = loans.size();
		}
		
		return LoanFactory.buildPagedLoanResponse(loans, page, pageSize, count);
	}

	@Override
	public float getLoanBalance(Loan loan) {
		
		if (loan == null || loan.getPayments() == null || loan.getPayments().isEmpty()) {
			return 0;
		}
		
		List<LoanPayment> payments = loan.getPayments();
		payments.sort(Comparator.comparing(LoanPayment::getPaymentNumber).reversed());
		
		Date today = new Date();
		
		Optional<LoanPayment> lastPay = payments.stream().filter(l -> l.getPaymentDate().before(today)).findFirst();		
		
		if (lastPay.isPresent()) {
			return lastPay.get().getBalance();
		} else {
			return loan.getPrincipal();
		}
	}

	@Override
	public int getCurrentLoanPaymentNumber(Loan loan) {

		if (loan == null || loan.getPayments() == null || loan.getPayments().isEmpty()) {
			return 0;
		}

		List<LoanPayment> payments = loan.getPayments();
		payments.sort(Comparator.comparing(LoanPayment::getPaymentNumber).reversed());

		Date today = new Date();

		Optional<LoanPayment> lastPay = payments.stream().filter(l -> l.getPaymentDate().before(today)).findFirst();

        return lastPay.map(loanPayment -> loanPayment.getPaymentNumber() + 1).orElse(1);
	}

	@Override
	public LoanResponse getLoanById(Integer userId, Integer loanId) {
		Optional<Loan> optLoan = loanRepository.findByIdAndUserId(loanId, userId);
		
		if (optLoan.isEmpty()) {
			log.info("Loan with id: {} and userId: {} not found", loanId, userId);
			return null;
		}
		
		return LoanFactory.buildLoanResponse(optLoan.get());
	}

	@Override
	@Transactional
	public void updateLoanBalances() {

		GenericSpecification<Loan> spec = new GenericSpecification<>();
		spec = spec.where("balance", 0, QueryOperation.GREATER_THAN);

		List<Loan> allLoans = loanRepository.findAll(spec.build());
		float balance;
		int paymentNumber;

		for (Loan l : allLoans) {
			balance = loanBusiness.getLoanBalance(l);
			paymentNumber = loanBusiness.getCurrentLoanPaymentNumber(l);

			if (l.getBalance() != balance || l.getCurrentPaymentNumber() != paymentNumber) {
				log.info("updating balance and payment number for loan {}", l.getId());
				l.setBalance(balance);
				l.setCurrentPaymentNumber(paymentNumber);
				loanRepository.save(l);
			} else {
				log.info("balance and payment number for loan {} is up to date", l.getId());
			}
		}
	}

	@Async
	@Override
	public void updateLoanBalancesAsync() {
		Instant start = Instant.now();
		log.info("Beginning Async task to update all loan balances");
		loanBusiness.updateLoanBalances();
		log.info("Completed Async task to update all loan balances. Execution time: {}", Duration.between(start, Instant.now()));
	}

	@Override
	public boolean deleteLoanById(Integer userId, Integer loanId) throws FinanceManagerException {

		//Verify loan exists and belongs to provided userId
		Optional<Loan> loan = loanRepository.findByIdAndUserId(loanId, userId);
		if (loan.isEmpty()) {
			throw new FinanceManagerException(myConfig.getGenericNotFoundMessage(), HttpStatus.NOT_FOUND);
		}

		//Delete transactions for this loan
		transactionBusiness.deleteByLoan(loan.get());
		loanRepository.deleteById(loanId);

		return true;
	}

	@Override
	public UserLoanSummaryResponse getUserLoanSummary(Integer userId) {
		Optional<User> user = userRepository.findById(userId);

		if (user.isEmpty()) {
			log.info("attempted to fetch loan summary for user with id {} but user was not found", userId);
			return null;
		}

		UserLoanSummaryResponse response = new UserLoanSummaryResponse();
		float balance = 0;
		float cost = 0;

		response.setCount(user.get().getLoans().size());

		for(Loan l : user.get().getLoans()) {
			balance += l.getBalance() != null ? l.getBalance() : 0;
			cost += l.getPayment() != null ? l.getPayment() : 0;
		}

		response.setMonthlyCost(cost);
		response.setCurrentDebt(balance);

		return response;

	}

	@Override
	@Transactional
	public LoanResponse updateLoanById(Integer userId, Integer loanId, LoanRequest request) throws FinanceManagerException {
		//First validate the request
		loanValidation.validateLoanRequest(request);

		//Next validate the loan exists
		Optional<Loan> l = loanRepository.findByIdAndUserId(loanId, userId);

		if(l.isEmpty()) {
			log.info("requested loan with id {} for user with id {} does not exist", loanId, userId);
			return null;
		}

		Loan loan = l.get();

		if(paymentChangesExist(loan, request)) {
			/* If payment related changes exist, we must recalculate and save the loan's payment details */

			log.info("loan update request contains changes that effect payment details. Loan must be recalculated");

			//Update fields
			loan.setName(request.getName());
			loan.setPrincipal(request.getPrincipal());
			loan.setFrequency(PaymentFrequencyEnum.valueOfLabel(request.getFrequency()));
			loan.setTerm(request.getTerm());
			loan.setRate(request.getRate());
			loan.setFirstPaymentDate(request.getFirstPaymentDate());

			//Recalculate our payment
			loan = calculateLoanPayment(loan);

			//Delete existing payment transactions
			//Recalculate the payment schedule (This will delete loan payments)
			transactionBusiness.deleteByLoan(loan);
			loan = calculatePaymentSchedule(loan);

			//Update audit fields
			loan.setAuditInfo(FinanceManagerUtil.updateAuditInfo(loan.getAuditInfo()));

			//Save loan
			loanRepository.saveAndFlush(loan);

			//Build and save Transactions
			transactionBusiness.buildAndSaveTransactionsForLoanPayments(loan.getPayments(), userId);

		} else if (nonPaymentChangesExist(loan, request)) {
			/* If changes exist but are not payment related, we must update those fields and save the loan */

			log.info("loan update request contains changes but does not effect payment details");

			//Update all non-payment related fields
			loan.setName(request.getName());

			//Save changes
			loanRepository.saveAndFlush(loan);
		} else {
			log.info("loan update request contains no changes. Skipping update");
		}

		//Build and return the response
		return LoanFactory.buildLoanResponse(loan);
	}

	@Override
	public CompareLoansResponse compareLoans(CompareLoansRequest request) {

		//First validate the request
		loanValidation.validateCompareLoanRequest(request);

		Loan loan = LoanFactory.buildLoanForCompareRequest(request.getOriginalLoan());
		Loan newLoan = LoanFactory.buildLoanForCompareRequest(request.getNewLoan());

		//Calculate Payment
		loan = calculateLoanPayment(loan);
		if(!Objects.equals(loan.getRate(), newLoan.getRate()) || !Objects.equals(loan.getTerm(), newLoan.getTerm())) {
			newLoan = calculateLoanPayment(newLoan);
		} else {
			newLoan.setPayment(loan.getPayment());
		}

		//Calculate Payment Schedule
		newLoan = calculatePaymentSchedule(newLoan);
		loan = calculatePaymentSchedule(loan);

		return LoanFactory.buildCompareLoansResponse(loan, newLoan);
	}

	/**
	 * Determines if any change exists between a saved Loan and a LoanRequest that would result in changes
	 * to the loan's payment details
	 * @param loan The loan to check changes against
	 * @param request The request containing the changes to check
	 * @return true if any payment related changes exists, false otherwise
	 */
	private boolean paymentChangesExist(Loan loan, LoanRequest request) {
        return loan.getFrequency() != PaymentFrequencyEnum.valueOfLabel(request.getFrequency())
                || !Objects.equals(loan.getRate(), request.getRate())
                || !Objects.equals(loan.getPrincipal(), request.getPrincipal())
                || !Objects.equals(loan.getTerm(), request.getTerm())
                || !loan.getFirstPaymentDate().equals(request.getFirstPaymentDate());
	}

	/**
	 * Determines if any change exists between a saved Loan and a LoanRequest that would not result in
	 * changes to the loan's payment details
	 * @param loan The loan to check changes against
	 * @param request The request containing the changes to check
	 * @return true if any non-payment related change exists, false otherwise
	 */
	private boolean nonPaymentChangesExist(Loan loan, LoanRequest request) {
		return !loan.getName().equals(request.getName());
	}

}
