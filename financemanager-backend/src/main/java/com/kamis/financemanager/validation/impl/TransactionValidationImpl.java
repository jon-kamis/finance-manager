package com.kamis.financemanager.validation.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.enums.TransactionTypeEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.rest.domain.transactions.TransactionPostRequest;
import com.kamis.financemanager.validation.TransactionValidation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TransactionValidationImpl implements TransactionValidation {

	@Autowired
	private YAMLConfig myConfig;

	@Override
	public void validateTransactionPostRequest(Integer userId, TransactionPostRequest request)
			throws FinanceManagerException {

		log.info("validating new loan request");

		if (userId == null || userId < 0) {
			throw new FinanceManagerException(myConfig.getUserIdRequiredError(), HttpStatus.BAD_REQUEST);
		}

		if (request.getAmount() == null || request.getAmount() < 0) {
			throw new FinanceManagerException(myConfig.getInvalidAmountErrorMsg(), HttpStatus.BAD_REQUEST);
		}

		if (request.getDaysOfMonth() == null || request.getDaysOfMonth().size() == 0) {
			throw new FinanceManagerException(myConfig.getAtLeastOneDateRequiredErrorMsg(), HttpStatus.BAD_REQUEST);
		} else {

			for (int day : request.getDaysOfMonth()) {

				if (day < 1 || day > 31) {
					throw new FinanceManagerException(myConfig.getInvalidDayOfMonthErrorMsg(), HttpStatus.BAD_REQUEST);

				}
			}
		}

		if (request.getEffectiveDate() == null) {
			throw new FinanceManagerException(myConfig.getEffectiveDateRequiredErrorMsg(), HttpStatus.BAD_REQUEST);
		}

		if (request.getCategory() == null || request.getCategory().isBlank()
				|| Arrays.asList(TransactionCategoryEnum.values()).stream()
						.noneMatch(c -> c.getCategory().equalsIgnoreCase(request.getCategory()))) {
			throw new FinanceManagerException(myConfig.getInvalidCategoryErrorMsg(), HttpStatus.BAD_REQUEST);
		}
		
		if (request.getFrequency() == null || request.getFrequency().isBlank()
				|| Arrays.asList(PaymentFrequencyEnum.values()).stream()
						.noneMatch(f -> f.getFrequency().equalsIgnoreCase(request.getFrequency()))) {
			throw new FinanceManagerException(myConfig.getInvalidFrequencyErrorMsg(), HttpStatus.BAD_REQUEST);
		}
		
		if (request.getType() == null || request.getType().isBlank()
				|| Arrays.asList(TransactionTypeEnum.values()).stream()
						.noneMatch(t -> t.getType().equalsIgnoreCase(request.getType()))) {
			throw new FinanceManagerException(myConfig.getInvalidTransactionTypeErrorMsg(), HttpStatus.BAD_REQUEST);
		}

	}

}
