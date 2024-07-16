package com.kamis.financemanager.validation.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.validation.FinanceManagerValidation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FinanceManagerValidationImpl implements FinanceManagerValidation {

	@Autowired
	private YAMLConfig myConfig;
	
	@Override
	public void validateSortType(String sortType) throws FinanceManagerException {

		if (sortType == null || sortType.isBlank()) {
			return;
		}
		
		
		if(!sortType.equalsIgnoreCase(FinanceManagerConstants.SORT_TYPE_ASC)
				&& !sortType.equalsIgnoreCase(FinanceManagerConstants.SORT_TYPE_DESC)) {
			log.debug("invalid sort type of {} for request", sortType);
			throw new FinanceManagerException(myConfig.getInvalidSortTypeErrorMsg(), HttpStatus.BAD_REQUEST);
		}

		
	}

}
