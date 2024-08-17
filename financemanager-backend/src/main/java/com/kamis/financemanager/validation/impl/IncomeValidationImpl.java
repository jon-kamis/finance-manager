package com.kamis.financemanager.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.validation.IncomeValidation;

@Component
public class IncomeValidationImpl implements IncomeValidation {

	@Autowired
	private YAMLConfig myConfig;

	@Override
	public void validateGetAllUserIncomesRequest(Integer userId, String sortBy, String sortType, Integer page,
			Integer pageSize) throws FinanceManagerException {

		// Validate UserId
		if (userId == null || userId < 0) {
			throw new FinanceManagerException(myConfig.getUserIdRequiredError(), HttpStatus.BAD_REQUEST);
		}

		// Validate SortBy
		if (sortBy != null && !sortBy.isBlank()
				&& !FinanceManagerConstants.INCOME_VALID_SORT_OPTIONS.contains(sortBy.toLowerCase())) {
			throw new FinanceManagerException(myConfig.getInvalidSortByErrorMsg(), HttpStatus.BAD_REQUEST);
		}

		// Validate SortType
		if (sortType != null && !sortType.isBlank()
				&& !FinanceManagerConstants.VALID_SORT_TYPES.contains(sortType.toLowerCase())) {
			throw new FinanceManagerException(myConfig.getInvalidSortTypeErrorMsg(), HttpStatus.BAD_REQUEST);
		}
		
		// Validate Paging parameters
		if ((page != null && page < 1) || (pageSize != null && pageSize < 1)) {
			throw new FinanceManagerException(myConfig.getInvalidPagingParameterErrorMsg(), HttpStatus.BAD_REQUEST);
		}

	}

}
