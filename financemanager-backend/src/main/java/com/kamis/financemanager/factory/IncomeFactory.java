package com.kamis.financemanager.factory;

import java.util.ArrayList;
import java.util.List;

import com.kamis.financemanager.database.domain.Income;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.enums.FilingTypeEnum;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.rest.domain.incomes.IncomePostRequest;
import com.kamis.financemanager.rest.domain.incomes.IncomeResponse;
import com.kamis.financemanager.rest.domain.transactions.TransactionResponse;
import com.kamis.financemanager.util.FinanceManagerUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IncomeFactory {

	public static Income createIncome(User user, IncomePostRequest request) {
		Income income = new Income();
		income.setName(request.getName());
		income.setTaxCredits(request.getTaxCredits());
		income.setWithheldTax(request.getTaxWithheld());
		income.setUser(user);
		income.setUserId(user.getId());
		income.setAmount(request.getAmount());
		income.setCategory(TransactionCategoryEnum.valueOfLabel(request.getCategory()));
		income.setTaxable(request.getTaxable());
		income.setAuditInfo(FinanceManagerUtil.getAuditInfo());
		income.setFrequency(PaymentFrequencyEnum.valueOfLabel(request.getFrequency()));
		income.setFilingType(FilingTypeEnum.valueOfLabel(request.getFilingType()));

		return income;
	}

	/**
	 * Builds a response for a single income object
	 * 
	 * @param income The income to build the response for
	 * @param transcation An optional transaction holding the income's transaction data
	 * @return An incomeResponse built from the given income
	 */
	public static IncomeResponse buildIncomeResponse(Income income, List<Transaction> transactions) {
		IncomeResponse response = new IncomeResponse();

		response.setId(income.getId());
		response.setName(income.getName());
		response.setTaxCredits(income.getTaxCredits());
		response.setUserId(income.getUserId());
		response.setWithheldTax(income.getWithheldTax());
		response.setAmount(income.getAmount());
		response.setCategory(income.getCategory().getCategory());
		response.setFrequency(income.getFrequency().getFrequency());

		if (income.getAuditInfo() != null) {
			response.setCreateDate(income.getAuditInfo().getCreateDt());
			response.setLastUpdateBy(income.getAuditInfo().getLastUpdateBy());
			response.setLastUpdateDate(income.getAuditInfo().getLastUpdateDt());
		} else {
			log.warn("expected audit info was not present for income with id: {}", income.getId());
		}
		
		//Add transactions
		if (transactions != null && !transactions.isEmpty()) {
			List<TransactionResponse> trs = new ArrayList<>();
			
			for (Transaction t : transactions) {
				trs.add(TransactionFactory.buildTransactionResponse(t));
			}
			
			response.setTransactions(trs);
		}

		return response;
	}

}
