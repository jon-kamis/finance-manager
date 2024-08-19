package com.kamis.financemanager.validation.impl;

import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.rest.domain.loans.LoanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.validation.FinanceManagerValidation;
import com.kamis.financemanager.validation.LoanValidation;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@Slf4j
@Component
public class LoanValidationImpl implements LoanValidation {

	@Autowired
	private YAMLConfig myConfig;
	
	@Autowired
	private FinanceManagerValidation financeManagerValidation;
	
	@Override
	public void validateGetAllLoansRequest(Integer userId, String sortBy,
			String sortType) throws FinanceManagerException {
		
		if (userId == null || userId <= 0) {
			log.debug("invalid userId for loan request");
			throw new FinanceManagerException(myConfig.getUserIdRequiredError(), HttpStatus.BAD_REQUEST);
		}
		
		if (sortBy != null && !sortBy.isBlank() 
				&& !FinanceManagerConstants.LOAN_VALID_SORT_TYPES.contains(sortBy)) {
			log.debug("invalid sortBy of {} for loan request", sortBy);
			throw new FinanceManagerException(myConfig.getInvalidSortByErrorMsg(), HttpStatus.BAD_REQUEST);
		}
		
		financeManagerValidation.validateSortType(sortType);
		
	}

	@Override
	public void validateLoanRequest(LoanRequest request) throws FinanceManagerException {

		boolean isValid = true;

		if (request == null) {
			throw new FinanceManagerException(myConfig.getGenericBadRequestMessage(), HttpStatus.BAD_REQUEST);
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

		if (request.getPrincipal() == null || request.getPrincipal() < 1) {
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

}
