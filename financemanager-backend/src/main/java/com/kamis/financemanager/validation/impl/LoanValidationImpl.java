package com.kamis.financemanager.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.validation.FinanceManagerValidation;
import com.kamis.financemanager.validation.LoanValidation;

import lombok.extern.slf4j.Slf4j;

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
			throw new FinanceManagerException(myConfig.getInvalidLoanSortingOptionsErrorMsg(), HttpStatus.BAD_REQUEST);
		}
		
		financeManagerValidation.validateSortType(sortType);
		
	}

}
