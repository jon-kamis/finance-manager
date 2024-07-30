package com.kamis.financemanager.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.business.IncomeBusiness;
import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.database.domain.Income;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.repository.IncomeRepository;
import com.kamis.financemanager.database.repository.TransactionRepository;
import com.kamis.financemanager.database.specifications.GenericSpecification;
import com.kamis.financemanager.database.specifications.QueryOperation;
import com.kamis.financemanager.enums.TableNameEnum;
import com.kamis.financemanager.exception.FinanceManagerException;
import com.kamis.financemanager.factory.IncomeFactory;
import com.kamis.financemanager.factory.TransactionFactory;
import com.kamis.financemanager.rest.domain.incomes.IncomePostRequest;
import com.kamis.financemanager.rest.domain.incomes.IncomeResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IncomeBusinessImpl implements IncomeBusiness {

	@Autowired
	private YAMLConfig myConfig;
	
	@Autowired
	private IncomeRepository incomeRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Override
	public boolean createIncome(Integer userId, IncomePostRequest request) throws FinanceManagerException {
		
		//First save new income
		Income income = incomeRepository.save(IncomeFactory.createIncome(userId, request));
		
		if (income.getId() == null) {
			log.error("attempted to fetch income id but value was null");
			throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		List<Transaction> transactions = new ArrayList<>();
		Transaction paycheck = TransactionFactory.buildTransactionFromIncomePostRequest(income, request);
		
		if(paycheck.getTransactionDays() == null || paycheck.getTransactionDays().isEmpty()) {
			log.error("expected request to have generated at least one transaction day but none were present");
			throw new FinanceManagerException(myConfig.getUnableToBuildTransactionDaysErrorMsg(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		transactions.add(paycheck);
		
		if (request.getTaxable().booleanValue()) {
			
		}
		
		//Perform save
		transactionRepository.saveAll(transactions);
		return true;
	}

	@Override
	public IncomeResponse findByUserIdAndId(Integer userId, Integer id) throws FinanceManagerException {
		
		GenericSpecification<Income> incomeSpec = new GenericSpecification<>();
		incomeSpec = incomeSpec.where("id", id, QueryOperation.EQUALS)
				.and("userId", userId, QueryOperation.EQUALS);
		
		Optional<Income> income = incomeRepository.findOne(incomeSpec.build());
		
		if(income.isEmpty()) {
			log.info("attempted to find income with id {} for user with id {} but none was found", id, userId);
			return null;
		}
		
		GenericSpecification<Transaction> transactionSpec = new GenericSpecification<>();
		transactionSpec = transactionSpec.where("parentId", income.get().getId(), QueryOperation.EQUALS)
				.and("parentTableName", TableNameEnum.INCOMES, QueryOperation.EQUALS_OBJECT);
		
		List<Transaction> transactions = transactionRepository.findAll(transactionSpec.build());
		
		return IncomeFactory.buildIncomeResponse(income.get(), transactions);
	}

	@Override
	public float calculateSimpleTaxes(Income income) throws FinanceManagerException {
		
		if (income.getFilingType() == null) {
			throw new FinanceManagerException(myConfig.getFilingTypeRequiredForTaxesError(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		float tax = 0;
		float credits = (income.getTaxCredits() * 2000) / (float)income.getFrequency().getNumPays();
		
		return tax;
	}
}
