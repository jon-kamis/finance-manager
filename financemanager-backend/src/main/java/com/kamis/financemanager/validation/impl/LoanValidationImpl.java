package com.kamis.financemanager.validation.impl;

import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.LoanManualPayment;
import com.kamis.financemanager.database.repository.LoanRepository;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.rest.domain.loans.CompareLoansRequest;
import com.kamis.financemanager.rest.domain.loans.LoanRequest;
import com.kamis.financemanager.rest.domain.loans.ManualLoanPaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.validation.FinanceManagerValidation;
import com.kamis.financemanager.validation.LoanValidation;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Component
public class LoanValidationImpl implements LoanValidation {

	@Autowired
	private YAMLConfig myConfig;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private FinanceManagerValidation financeManagerValidation;
	
	@Override
	public void validateGetAllLoansRequest(Integer userId, String sortBy,
			String sortType) throws FinanceManagerException {
		
		if (userId == null || userId <= 0) {
			log.debug("invalid userId for loan request");
			throw new FinanceManagerException(myConfig.getUserIdRequiredError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		if (sortBy != null && !sortBy.isBlank() 
				&& !FinanceManagerConstants.LOAN_VALID_SORT_TYPES.contains(sortBy)) {
			log.debug("invalid sortBy of {} for loan request", sortBy);
			throw new FinanceManagerException(myConfig.getInvalidSortByErrorMsg(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		financeManagerValidation.validateSortType(sortType);
		
	}

	@Override
	public void validateLoanRequest(LoanRequest request) throws FinanceManagerException {

		boolean isValid = true;

		if (request == null) {
			throw new FinanceManagerException(myConfig.getGenericUnprocessableError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		if (request.getName() == null || request.getName().isBlank()) {
			log.info("invalid request. name is required");
			isValid = false;
		}

		if (request.getTerm() == null || request.getTerm() < 1 ) {
			log.info("invalid request. term is required");
			isValid = false;
		}

		if (request.getRate() == null || request.getRate() < 0) {
			log.info("invalid request. rate is required");
			isValid = false;
		}

		if (request.getFrequency() == null ||
				PaymentFrequencyEnum.valueOfLabel(request.getFrequency()) == null) {
			log.info("invalid request. frequency is required");
			isValid = false;
		}

		if (request.getPrincipal() == null || request.getPrincipal() <= 0) {
			log.info("invalid request. principal is required");
			isValid = false;
		}

		if (request.getFirstPaymentDate() == null) {
			log.info("invalid request. first payment date is required");
			isValid = false;
		}

		if (!isValid) {
			throw new FinanceManagerException(myConfig.getGenericUnprocessableError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@Override
	public void validateCompareLoanRequest(CompareLoansRequest request) throws FinanceManagerException {

		if (request.getNewLoan() == null || request.getOriginalLoan() == null) {
			log.debug("new and original loans are required");
			throw new FinanceManagerException(myConfig.getGenericUnprocessableError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		if (request.getOriginalLoan().getPrincipal() == null || request.getOriginalLoan().getPrincipal() <= 0
			|| request.getNewLoan().getPrincipal() == null || request.getNewLoan().getPrincipal() <= 0) {
			log.debug("principal must be greater than 0");
			throw new FinanceManagerException(myConfig.getGenericUnprocessableError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		if (request.getOriginalLoan().getRate() == null || request.getOriginalLoan().getRate() < 0
			|| request.getNewLoan().getRate() == null || request.getNewLoan().getRate() < 0) {
			log.debug("rate must be at least 0");
			throw new FinanceManagerException(myConfig.getGenericUnprocessableError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		if (request.getOriginalLoan().getTerm() == null || request.getOriginalLoan().getTerm() <= 0
			|| request.getNewLoan().getTerm() == null || request.getNewLoan().getTerm() <= 0) {
			log.debug("term must be at least 1");
			throw new FinanceManagerException(myConfig.getGenericUnprocessableError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@Override
	public void validateCreatePaymentRequest(int loanId, ManualLoanPaymentRequest request) {

		Optional<Loan> l = loanRepository.findById(loanId);

		if (l.isEmpty()) {
			log.debug("loan not found");
			throw new FinanceManagerException(myConfig.getGenericNotFoundMessage(), HttpStatus.NOT_FOUND);
		}

		//Amount is required
		if (request.getAmount() == null || request.getAmount() < 0) {
			log.debug("amount must be not null and at least 0");
			throw new FinanceManagerException(myConfig.getPaymentAmountRequiredError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		//Effective date is required
		if (request.getEffectiveDate() == null) {
			log.debug("effective date is required");
			throw new FinanceManagerException(myConfig.getPaymentEffectiveDateRequiredError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		/*
		Date Validate Criteria
		1. IF expiration date is set
			a. Cannot overlap any other payment that has expiration date set
			b. If a payment exists without an expiration date, its effective date cannot be between effective and expiration date of this request
		2. IF expiration date is not set
			a. Cannot have effective date between any existing payment's effective and expiration dates
		 */
		if (l.get().getManualPayments() != null && !l.get().getManualPayments().isEmpty()) {
			if (request.getExpirationDate() != null) {

				for (LoanManualPayment p : l.get().getManualPayments()) {

					if (p.getExpirationDate() != null &&
							!request.getEffectiveDate().after(p.getExpirationDate()) && !request.getExpirationDate().before(p.getEffectiveDate())) {
						log.info("request overlaps existing payment with id {}", p.getId());
						throw new FinanceManagerException(myConfig.getPaymentExistsError(), HttpStatus.UNPROCESSABLE_ENTITY);
					} else if (!p.getEffectiveDate().after(request.getExpirationDate()) && !p.getEffectiveDate().before(request.getEffectiveDate())) {
						log.info("request overlaps existing non-expiring payment with id {}", p.getId());
						throw new FinanceManagerException(myConfig.getPaymentExistsError(), HttpStatus.UNPROCESSABLE_ENTITY);
					}
				}

			} else {

				for (LoanManualPayment p : l.get().getManualPayments()) {

					if (p.getExpirationDate() != null
							&& ((!p.getEffectiveDate().after(request.getEffectiveDate())) && !p.getExpirationDate().before(request.getEffectiveDate()))) {
						log.info("request begins in the middle of another payment");
						throw new FinanceManagerException(myConfig.getPaymentExistsError(), HttpStatus.UNPROCESSABLE_ENTITY);
					}
				}
			}
		}
	}
}
