package com.kamis.financemanager.factory;

import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.domain.TransactionDay;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.enums.TransactionTypeEnum;
import com.kamis.financemanager.rest.domain.transactions.TransactionPostRequest;
import com.kamis.financemanager.util.FinanceManagerUtil;

public class TransactionFactory {

	/**
	 * Builds a new Transaction object based on a post request
	 * 
	 * @param userId  The id of the user to build the request for
	 * @param request The request containing information on how to build the new
	 *                transaction
	 * @return A Transaction object built from the given request
	 */
	public static Transaction buildTransactionFromPostRequest(Integer userId, TransactionPostRequest request) {
		Transaction transaction = new Transaction();

		transaction.setName(request.getName());
		transaction.setUserId(userId);
		transaction.setAmount(request.getAmount());
		transaction.setCategory(TransactionCategoryEnum.valueOfLabel(request.getCategory()));
		transaction.setFrequency(PaymentFrequencyEnum.valueOfLabel(request.getFrequency()));
		transaction.setType(TransactionTypeEnum.valueOfLabel(request.getType()));
		transaction.setEffectiveDate(request.getEffectiveDate());
		transaction.setExpirationDate(request.getExpirationDate());
		transaction.setAuditInfo(FinanceManagerUtil.getAuditInfo());

		if (request.getDaysOfMonth() != null && !request.getDaysOfMonth().isEmpty()) {
			for (int day : request.getDaysOfMonth()) {
				transaction.addTransactionDay(buildTransactionDay(day));
			}
		}

		return transaction;
	}

	/**
	 * Builds a new TransactionDay object
	 * 
	 * @param day The day of the transaction
	 * @return A TransactionDay object
	 */
	public static TransactionDay buildTransactionDay(int day) {
		TransactionDay transactionDay = new TransactionDay();
		transactionDay.setDay(day);
		transactionDay.setAuditInfo(FinanceManagerUtil.getAuditInfo());
		return transactionDay;
	}
}
